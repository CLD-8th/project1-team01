# VPC 네트워크 설계 (펩시미만잡)

CIDR 표준(교안 예시, 팀 값으로 조정 가능):

```
VPC 10.0.0.0/16  (ap-northeast-2)
│
├── 가용 영역 ap-northeast-2a
│   ├── 퍼블릭 서브넷          10.0.0.0/20    ← ALB, NAT Gateway
│   ├── 프라이빗 서브넷 (App)   10.0.32.0/20   ← EC2 / Fargate 태스크
│   └── 프라이빗 서브넷 (Data)  10.0.64.0/20   ← RDS, ElastiCache
│
└── 가용 영역 ap-northeast-2c
    ├── 퍼블릭 서브넷          10.0.16.0/20
    ├── 프라이빗 서브넷 (App)   10.0.48.0/20
    └── 프라이빗 서브넷 (Data)  10.0.80.0/20
```

| 대상 | 경로 |
|---|---|
| 퍼블릭 | `0.0.0.0/0` → Internet Gateway |
| 프라이빗(App) | `0.0.0.0/0` → 같은 AZ의 NAT Gateway |
| 프라이빗(Data) | VPC 내부 통신만(외부 경로 없음) |
