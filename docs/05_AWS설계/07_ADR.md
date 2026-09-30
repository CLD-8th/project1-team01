# ADR (Architecture Decision Record) — 펩시미만잡

> 주요 설계 결정 3건 이상. 발표 질의응답의 근거 자료.

---

## ADR-01: 컴퓨팅 티어로 EC2 Auto Scaling을 선택

**상태**: 확정 (2026-09-30)

**배경**: 피크 2.4 RPS, 급증 시 7.2 RPS. 가용성 목표 99.9%. 팀원 전원이 VM + Docker 운영 경험은 있으나 ECS 경험은 없음.

**검토한 대안**:
1. EC2 Auto Scaling + ALB
2. ECS Fargate + ALB

**결정**: 1번. 최소 2 · 희망 2 · 최대 3, 2개 AZ 분산.

**근거**:
- 로컬 Docker 운영 방식과 동일해 설계 검증과 운영 이해가 쉬움
- 트래픽이 작고 안정적이라 인스턴스 단위 과금이 불리하지 않음
- 별도 개념(태스크 정의 등)을 새로 배우지 않아도 됨

**결과와 위험**:
- OS 패치·AMI 관리 부담 발생 → 시작 템플릿 버전 관리로 대응
- 트래픽 변동이 커지거나 운영 인력이 부족해지면 Fargate 재검토

---

## ADR-02: RDS Multi-AZ 적용

**상태**: 확정

**배경**: RTO 30분·RPO 5분·가용성 목표 99.9%. Single-AZ는 장애 시 수동 복구라 RTO 달성이 어려움.

**검토한 대안**:
1. Single-AZ + 자동 백업
2. Multi-AZ(동기 복제, 자동 장애 조치)

**결정**: 2번. Multi-AZ 채택.

**근거**:
- 영속 데이터(도서·거래·회원 정보)라 유실·중단 허용도가 낮음
- Multi-AZ 추가 비용 대비 다운타임 비용(손익분기 약 13분, 09-22 교안 방식 적용)이 더 큼

**결과와 위험**:
- 비용 약 2배 증가 → NAT Gateway·ElastiCache 절감으로 일부 상쇄

---

## ADR-03: NAT Gateway 개수 — AZ당 1개(총 2개) 대신 1개만 사용

**상태**: 확정

**배경**: NAT Gateway는 트래픽량과 무관하게 시간당 고정 요금. AZ 2개분 배치 시 소규모 서비스(피크 7.2 RPS) 대비 비용 비중이 과함. AWS 공식 문서는 "고가용성을 위해 AZ마다 1개씩 두는 것"을 권장(should)하지만 필수 요구사항(must)은 아님 — 다른 AZ의 서브넷이 라우팅 테이블 설정만으로 타 AZ의 NAT Gateway를 공유하는 것은 기술적으로 유효한 구성.

**검토한 대안**:
1. AZ마다 NAT Gateway 배치(2개, Zonal) — AWS 권장 방식
2. NAT Gateway 1개만 배치(Zonal), 두 AZ의 프라이빗(App) 서브넷이 공유
3. Regional NAT Gateway(자동 멀티 AZ 확장·페일오버 지원 신규 옵션) — 과금이 AZ 단위로 부과되어 사실상 1번과 비용이 동일, 관리 편의성만 얻는 옵션이라 우리 목적(비용 절감)과 안 맞아 기각

**결정**: 2번. Zonal NAT Gateway 1개만 배치.

**근거**:
- 사용자 트래픽 경로(ALB→앱→RDS/ElastiCache)는 NAT를 거치지 않아 서비스 가용성과 무관
- 아웃바운드 트래픽 자체가 적어 이중화 효과 대비 비용이 맞지 않음
- AWS 권장사항(1번)과 다르게 간 것이지만, 그 트레이드오프를 인지하고 의도적으로 선택함(권장사항을 몰라서 놓친 게 아님)

**결과와 위험**:
- NAT가 없는 AZ는 장애 시 아웃바운드(패키지 업데이트 등)만 일시 불가, 사용자 트래픽엔 영향 없음
- AZ 경계를 넘는 소액의 데이터 전송 비용 발생(물량 적어 무시 가능)

**참고 자료**:
- [Regional NAT gateways for automatic multi-AZ expansion - Amazon VPC](https://docs.aws.amazon.com/vpc/latest/userguide/nat-gateways-regional.html)
- [Create NAT Gateways in at Least Two Availability Zones - Trend Micro](https://www.trendmicro.com/cloudoneconformity/knowledge-base/aws/VPC/nat-gateways-in-at-least-two-availability-zones.html)

---

## ADR-04: ElastiCache Replica 미적용(단일 노드)

**상태**: 확정

**배경**: 랭킹 캐시(Sorted Set)는 DB에서 재생성 가능한 비영속 데이터.

**검토한 대안**:
1. Primary + Replica(이중화)
2. 단일 노드

**결정**: 2번. 단일 노드.

**근거**:
- 유실돼도 서비스 중단이 아니라 랭킹 재계산으로 복구 가능
- 트래픽이 작아 읽기 분산도 불필요

**결과와 위험**:
- 캐시 노드 장애 시 랭킹 조회가 일시적으로 지연(DB 직접 조회로 대체)
- 트래픽 늘면 Replica 추가로 대응(구조 변경 없이 컴포넌트만 추가)

---

## ADR-05: Auto Scaling 최소 대수 2

**상태**: 확정

**배경**: 급증 RPS 7.2는 1대로도 충분하지만, 가용성 목표 99.9%는 AZ 이중화를 요구함.

**검토한 대안**:
1. 1대만 운영
2. 2대(AZ당 1대) 상시 운영

**결정**: 2번.

**근거**:
- AZ 하나가 죽어도 나머지가 즉시 트래픽 처리(무중단)
- t4g.small 2대 ≈ 월 $25 수준이라 비용 부담은 작음

**결과와 위험**:
- 프로젝트 규모 대비 대수가 많아 보일 수 있음 → 실제 비용 영향은 적다는 점으로 방어
