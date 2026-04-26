<!-- Glowly API — Backend SaaS de Gestão para o Nicho Beauty -->
<p align="center">
  <img src="https://readme-typing-svg.herokuapp.com?font=Inter&weight=600&size=28&pause=1000&color=C084FC&center=true&vCenter=true&width=435&lines=Glowly+%E2%9C%A8;Beleza+%2B+Tecnologia" alt="Glowly" />
</p>

<p align="center">
  <b>SaaS de agenda e gestão para salões de beleza, estéticas e profissionais do nicho beauty.</b><br>
  <sub>Organize agendamentos, reduza faltas, automatize confirmações e potencialize seu faturamento.</sub>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white" alt="Spring Security" />
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black" alt="Swagger" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/status-em%20desenvolvimento-C084FC?style=flat-square" alt="Status" />
  <img src="https://img.shields.io/badge/license-MIT-9cf?style=flat-square" alt="License" />
</p>

---

> 🚧 **Este projeto está em desenvolvimento ativo.**  
> A base de autenticação, autorização e gestão de lojas está consolidada. Os módulos de agenda, WhatsApp e relatórios estão no roadmap. Feedbacks e contribuições são bem-vindos!

---

## 💜 O que é o Glowly?

O **Glowly** é uma plataforma SaaS completa pensada para transformar a gestão de negócios de beleza com tecnologia prática e eficiente.

Nosso objetivo é simples: **eliminar a bagunça de planilhas, cadernos e mensagens soltas no WhatsApp**, entregando uma experiência moderna de agendamento, controle de clientes e automação de comunicação.

### Para quem é?
- 💇‍♀️ Salões de beleza e cabeleireiros
- 💆‍♀️ Clínicas de estética e spa
- 💅 Manicures, depiladoras e profissionais autônomos
- 🏢 Pequenas e médias empresas do nicho beauty

---

## ✨ Funcionalidades (MVP Atual)

### 🔐 Autenticação & Segurança
- Login com JWT stateless e controle de sessões via `tokenVersion`
- Cadastro com validação de CPF, email e telefone
- Recuperação de senha via email (Resend) com tokens temporários hasheados
- Proteção contra brute-force (lockout temporário após tentativas excessivas)
- Rate limiting inteligente por IP com proteção contra spoofing de proxy
- RBAC: roles `ADMIN` e `USER` com hierarquia de permissões

### 🏪 Gestão de Lojas (Base)
- Cadastro e estrutura inicial de lojas vinculadas a contas de usuário
- Arquitetura modular pronta para expansão

### 📚 Documentação & DevEx
- OpenAPI/Swagger UI integrado
- Configuração via variáveis de ambiente
- Docker e Docker Compose prontos para uso

---

## 🗺️ Roadmap

| Fase | Módulo | Status |
|:---|:---|:---|
| **Fase 1** | Autenticação, autorização e gestão de contas | ✅ Concluído |
| **Fase 1** | Gestão de lojas e estrutura base | 🔄 Em andamento |
| **Fase 2** | Agenda online e calendário inteligente | 📋 Planejado |
| **Fase 2** | Link de autoagendamento para clientes | 📋 Planejado |
| **Fase 3** | Cadastro de clientes e histórico de atendimentos | 📋 Planejado |
| **Fase 3** | Lembretes automáticos via WhatsApp | 📋 Planejado |
| **Fase 4** | Relatórios de faturamento e produtividade | 📋 Planejado |
| **Fase 4** | Painel administrativo e métricas | 📋 Planejado |
| **Fase 5** | App mobile (React Native / Flutter) | 📋 Futuro |

---

## 🛠️ Stack Tecnológica

| Camada | Tecnologia |
|:---|:---|
| **Linguagem** | Kotlin 2.2.x |
| **Framework** | Spring Boot 3.4.x |
| **Segurança** | Spring Security + JWT (JJWT) |
| **Banco de Dados** | PostgreSQL |
| **ORM / Persistência** | Spring Data JPA (Hibernate) |
| **Cache & Rate Limit** | Bucket4j + Caffeine |
| **Email** | Resend Java SDK |
| **Documentação** | springdoc-openapi (Swagger UI) |
| **Build** | Gradle (wrapper) |
| **Containerização** | Docker + Docker Compose |
| **Java Runtime** | Eclipse Temurin 17 |

---

## 🚀 Como Rodar

### Pré-requisitos
- Java 17+
- Docker (recomendado)
- Gradle (ou use o wrapper `./gradlew`)

### 1) Clone o repositório
```bash
git clone https://github.com/seu-usuario/glowly-api.git
cd glowly-api
```

### 2) Configure as variáveis de ambiente
Crie um arquivo `.env` na raiz:
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/glowly_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JWT_SECRET=sua-chave-jwt-super-segura-com-pelo-menos-32-caracteres
SPRING_JWT_EXPIRATION_DAYS=7
APP_FRONTEND_URL=http://localhost:3000
APP_SWAGGER_URL=http://localhost:8080
RESEND_API_KEY=re_sua_chave_aqui
```

### 3A) Rodar com Docker Compose (recomendado)
```bash
docker compose build
docker compose up -d

# Logs
docker compose logs -f api

# Parar
docker compose down
```

### 3B) Rodar localmente com Gradle
```bash
# Inicie o PostgreSQL
docker run -d \
  --name glowly-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=glowly_db \
  -p 5432:5432 \
  postgres:15-alpine

# Rode a aplicação
./gradlew bootRun
```

Acesse a documentação: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## ⚙️ Variáveis de Ambiente

### 🔑 Essenciais

| Variável | Descrição | Padrão |
|:---|:---|:---|
| `SPRING_JWT_SECRET` | Segredo para assinar/validar JWTs (mín. 32 caracteres) | — |
| `SPRING_JWT_EXPIRATION_DAYS` | Validade do token JWT em dias | `7` |
| `SPRING_DATASOURCE_URL` | JDBC URL do PostgreSQL | — |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | — |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | — |
| `SPRING_DATASOURCE_DRIVER` | Driver JDBC | `org.postgresql.Driver` |

### 🌐 Aplicação

| Variável | Descrição | Padrão |
|:---|:---|:---|
| `APP_FRONTEND_URL` | URL do front-end (usada em emails) | `http://localhost:3000` |
| `APP_SWAGGER_URL` | URL pública da API | `http://localhost:8080` |
| `APP_TRUSTED_PROXIES` | IPs de reverse proxies confiáveis (separados por vírgula) | — |

### 📧 Notificações

| Variável | Descrição |
|:---|:---|
| `RESEND_API_KEY` | Chave da API Resend para envio de emails |

### 🔒 Segurança & Cadastro

| Variável | Descrição | Padrão |
|:---|:---|:---|
| `APP_MIN_FULLNAME_LENGTH` | Mínimo de caracteres para nome completo | `7` |
| `APP_MIN_USERNAME_LENGTH` | Mínimo de caracteres para username | `4` |
| `APP_MAX_USERNAME_LENGTH` | Máximo de caracteres para username | `20` |
| `APP_MIN_PASSWORD_LENGTH` | Mínimo de caracteres para senha | `6` |
| `APP_MAX_PASSWORD_LENGTH` | Máximo de caracteres para senha | `30` |
| `APP_MAX_ATTEMPTS` | Tentativas de login antes do lockout | `5` |
| `APP_LOCKOUT_MINUTES` | Tempo de bloqueio em minutos | `5` |
| `APP_TOKEN_EXPIRATION_MINUTES` | Expiração do token de recuperação de senha | `5` |
| `APP_MAX_REQUESTS_PER_MINUTES` | Limite de requisições por minuto | `40` |

---

## 🐳 Docker (Produção)

```bash
# Build
docker build -t seu-usuario/glowly-api .

# Push
docker push seu-usuario/glowly-api

# Run
docker run -d \
  --name glowly-api \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://seu-db:5432/glowly_db" \
  -e SPRING_DATASOURCE_USERNAME="postgres" \
  -e SPRING_DATASOURCE_PASSWORD="sua-senha-segura" \
  -e SPRING_JWT_SECRET="sua-chave-jwt-segura" \
  -e RESEND_API_KEY="sua-api-key" \
  -e APP_TRUSTED_PROXIES="10.0.0.1,10.0.0.2" \
  seu-usuario/glowly-api
```

---

## 🧪 Testes

```bash
./gradlew test
```

---

## 🧭 Documentação da API

- **OpenAPI JSON:** `/v3/api-docs`
- **Swagger UI:** `/swagger-ui/index.html`

---

## 🛠️ Debug & Problemas Comuns

### "Conexão recusada ao banco"
- Verifique se `SPRING_DATASOURCE_URL` está correto
- Confirme que o container PostgreSQL está rodando: `docker ps`
- Em Docker Compose, o host do banco é `db` (nome do serviço)

### "JWT inválido ou expirado"
- Verifique se `SPRING_JWT_SECRET` é idêntico entre ambiente de desenvolvimento e produção
- A secret deve ter pelo menos 32 caracteres

### "Email de recuperação não chega"
- Confirme a `RESEND_API_KEY`
- Verifique os logs da aplicação para detalhes de erro da API Resend

### "Rate limit bloqueando requisições legítimas"
- Ajuste `APP_MAX_REQUESTS_PER_MINUTES` ou configure `APP_TRUSTED_PROXIES` corretamente

---

## 📝 Estrutura do Projeto

```
glowly-api/
├── src/
│   ├── main/
│   │   ├── kotlin/com/glowly/
│   │   │   ├── configs/              # Configurações do Spring
│   │   │   ├── identity/             # Módulo de autenticação e usuários
│   │   │   │   ├── controllers/      # Endpoints REST (Auth, Admin)
│   │   │   │   ├── services/         # Lógica de negócio
│   │   │   │   ├── repositories/     # Acesso a dados (JPA)
│   │   │   │   ├── security/         # JWT, filtros, BCrypt
│   │   │   │   ├── models/           # Entidades (User, PasswordResetToken)
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── enums/            # Roles, AccountStatus
│   │   │   │   └── exceptions/       # Handler global de exceções
│   │   │   ├── stores/               # Módulo de gestão de lojas
│   │   │   │   ├── controllers/
│   │   │   │   ├── services/
│   │   │   │   ├── repositories/
│   │   │   │   ├── models/
│   │   │   │   └── dto/
│   │   │   └── GlowlyApiApplication.kt
│   │   └── resources/
│   │       ├── application.yml       # Configurações principais
│   │       └── META-INF/spring/      # Auto-configurações
│   └── test/                         # Testes unitários e de integração
├── docker-compose.yml
├── Dockerfile
├── build.gradle.kts
└── README.md
```

---

## 🤝 Contribuição

Este é um projeto em evolução. Sugestões, issues e pull requests são muito bem-vindos!

1. Faça um fork do projeto
2. Crie uma branch (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'feat: adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

---

## 📄 Licença

Distribuído sob licença MIT. Veja o arquivo [`LICENSE`](LICENSE) para mais informações.

---

<p align="center">
  <sub>Feito com 💜 por <strong>Gabriel Lins</strong></sub>
</p>
