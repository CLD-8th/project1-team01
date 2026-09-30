# VPC 네트워크 설계 (펩시미만잡)

CIDR 표준(교안 예시 그대로 사용):

```
VPC 10.0.0.0/16  (ap-northeast-2)
│
├── 가용 영역 ap-northeast-2a
│   ├── 퍼블릭 서브넷          10.0.0.0/20    ← ALB, NAT Gateway (1개, ADR-03)
│   ├── 프라이빗 서브넷 (App)   10.0.32.0/20   ← EC2 (app 컨테이너, 8090)
│   └── 프라이빗 서브넷 (Data)  10.0.64.0/20   ← RDS Primary, ElastiCache (단일 노드, ADR-04)
│
└── 가용 영역 ap-northeast-2c
    ├── 퍼블릭 서브넷          10.0.16.0/20   ← ALB
    ├── 프라이빗 서브넷 (App)   10.0.48.0/20   ← EC2 (app 컨테이너, 8090)
    └── 프라이빗 서브넷 (Data)  10.0.80.0/20   ← RDS Standby (Multi-AZ, ADR-02)
```

| 대상 | 경로 |
|---|---|
| 퍼블릭 | `0.0.0.0/0` → Internet Gateway |
| 프라이빗(App) 2a·2c 공통 | `0.0.0.0/0` → 2a의 NAT Gateway(비용 절감, ECR 이미지 pull·패키지 설치용) |
| 프라이빗(Data) | VPC 내부 통신만(외부 경로 없음) |

NAT Gateway를 2a에만 두고 2c의 프라이빗(App) 서브넷도 이걸 공유하도록 라우팅함(ADR-03). 이 때문에 2c에서 2a로 AZ 경계를 넘는 아웃바운드 트래픽에 소액의 데이터 전송 비용이 붙지만, 물량이 적어 무시 가능한 수준.
