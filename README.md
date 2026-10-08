# TicketFlow - Sistema de Venda de Ingressos

Sistema para gestão e venda de ingressos de eventos, com backend em Java/Spring Boot e interface em Angular. O projeto permite cadastrar eventos, consultar disponibilidade, realizar compras e processar pagamentos por meio do Mercado Pago.

## Visão geral

O TicketFlow foi pensado para facilitar a criação e comercialização de eventos com uma experiência simples para o usuário final. A aplicação oferece:

- cadastro e listagem de eventos;
- busca e paginação de eventos;
- criação de compras com validação de dados;
- integração com checkout do Mercado Pago;
- atualização automática do status de pagamento via webhook;
- geração de dados iniciais para demonstração quando o banco estiver vazio.

## Stack tecnológica

- Java 21
- Spring Boot 4.x
- Spring Data JPA
- Maven
- MySQL
- Mercado Pago API
- Angular (frontend em projeto separado)

## Estrutura do projeto

```text
TicketFlow-Sistema-de-Venda-de-Ingressos/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── TicketFlow_Sistema_de_Venda_de_Ingressos/api/
│   │   │       ├── Config/
│   │   │       ├── Controller/
│   │   │       ├── DTO/
│   │   │       ├── Exception/
│   │   │       ├── Model/
│   │   │       ├── Payment/
│   │   │       ├── Repository/
│   │   │       ├── Service/
│   │   │       └── ApiApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── .gitignore
├── HELP.md
├── LICENSE
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── target/
```

## Requisitos

Antes de executar o projeto, certifique-se de ter instalado:

- JDK 21+
- Maven ou usar o wrapper do projeto (`mvnw`)
- MySQL em execução localmente
- Node.js 18+ e npm (se for executar o frontend Angular)
- Credenciais de teste do Mercado Pago

## Configuração do banco de dados

A aplicação usa MySQL com a URL padrão:

```text
jdbc:mysql://localhost:3306/ticketflow?createDatabaseIfNotExist=true
```

Se o banco ainda não existir, o driver tentará criá-lo, desde que o usuário configurado tenha permissão adequada.

### Variáveis de ambiente do backend

No PowerShell, configure as variáveis antes de iniciar a aplicação:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/ticketflow?createDatabaseIfNotExist=true"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "sua_senha"
$env:DB_DRIVER = "com.mysql.cj.jdbc.Driver"
```

Observações:

- não armazene senhas diretamente no repositório;
- a aplicação usa `application.properties` com valores padrão, mas as variáveis de ambiente podem sobrescrevê-los;
- se quiser apontar para outra instância MySQL, basta alterar `DB_URL`.

## Configuração do Mercado Pago

A compra de ingressos usa o Checkout Pro do Mercado Pago. Para habilitar esse fluxo, defina as variáveis abaixo antes de iniciar a API:

```powershell
$env:MP_ACCESS_TOKEN = "TEST-seu-access-token"
$env:MP_WEBHOOK_SECRET = "seu-segredo-de-webhook"
$env:MP_NOTIFICATION_URL = "https://sua-url-publica/api/payments/mercadopago/webhook"
$env:MP_SANDBOX = "true"
$env:FRONTEND_URL = "http://localhost:4200"
```

Importante:

- `MP_NOTIFICATION_URL` precisa ser publicamente acessível por HTTPS para o Mercado Pago enviar o webhook;
- em desenvolvimento, geralmente é necessário usar um túnel HTTPS para expor a aplicação;
- sem `MP_ACCESS_TOKEN`, a criação de pagamentos retorna `503`;
- as credenciais devem permanecer fora do controle de versão.

## Executando o backend

Na raiz do projeto, execute:

```powershell
.\mvnw.cmd spring-boot:run
```

A API será iniciada em:

```text
http://localhost:8081
```

A porta padrão pode ser ajustada em `application.properties`.

## Executando o frontend Angular

Com a API ativa, abra outro terminal e inicie o frontend em uma pasta separada, por exemplo:

```powershell
Set-Location '..\TicketFlow Front'
npm install
npm start -- --port 4200
```

A aplicação frontend fica em:

```text
http://localhost:4200/
```

A API e o frontend se comunicam via CORS, e o backend já está configurado para aceitar requisições vindas de `http://localhost:4200`.

## Funcionalidades principais

### Gestão de eventos

- criação de eventos com título, descrição, data, local, categoria e preço;
- listagem paginada e busca por nome ou texto relacionado;
- consulta por identificador único do evento;
- suporte a eventos gratuitos, quando `precoIngresso` é omitido ou zero.

### Compras e pagamentos

- validação de CPF, quantidade e tipo de pagamento;
- cálculo do valor total com base no evento;
- suporte aos métodos: `pix`, `credit-card` e `boleto`;
- geração de checkout no Mercado Pago;
- confirmação do status da compra por webhook ou consulta direta.

### Dados iniciais

Ao iniciar a aplicação com o banco vazio, o sistema carrega automaticamente cinco eventos de demonstração, como:

- Festival de Música ao Vivo
- Festival Gastronômico
- Noite de Stand-up
- Conferência de Tecnologia
- Feira de Arte e Design

## Endpoints principais

### Eventos

#### POST /api/events
Cria um novo evento.

Exemplo de payload:

```json
{
  "titulo": "Festival de Música ao Vivo",
  "descricao": "Uma noite com bandas e artistas brasileiros em vários palcos.",
  "dataHora": "2026-11-20T19:00:00",
  "local": "Parque Villa-Lobos, São Paulo",
  "imagemUrl": "https://exemplo.com/imagem.jpg",
  "categoria": "Música",
  "precoIngresso": 90.00
}
```

#### GET /api/events
Lista eventos com paginação e filtros opcionais.

Parâmetros:

- `status` (opcional): `todos` (padrão)
- `q` (opcional): termo de busca
- `page`, `size`, `sort` (via paginação do Spring)

#### GET /api/events/{id}
Retorna um evento específico pelo ID.

### Compras

#### POST /api/purchases
Cria uma compra com geração de checkout para pagamento.

Exemplo de payload:

```json
{
  "eventId": 1,
  "customerName": "Pessoa Compradora",
  "email": "comprador@example.com",
  "cpf": "529.982.247-25",
  "phone": "(11) 99999-9999",
  "quantity": 2,
  "paymentMethod": "pix"
}
```

Resposta de sucesso:

- status `201 Created`
- dados da compra
- total calculado
- status da compra
- `checkoutUrl` quando aplicável

#### GET /api/purchases/{id}
Consulta o status atual de uma compra.

#### Webhook do Mercado Pago

O webhook do provedor atualiza o status da compra para valores como:

- `approved`
- `failed`
- `refunded`

## Fluxo de compra em produção

1. O administrador cria um evento.
2. O cliente envia `POST /api/purchases`.
3. A API valida os dados do comprador e calcula o total.
4. Se o ingresso for gratuito, a compra é confirmada imediatamente.
5. Caso contrário, a API cria o checkout do Mercado Pago.
6. O comprador finaliza o pagamento fora da aplicação.
7. O webhook do Mercado Pago informa o status atualizado.
8. A aplicação consulta o status com `GET /api/purchases/{id}` para mostrar o resultado ao usuário.

## Compilação e empacotamento

Para gerar o artefato da aplicação sem iniciar o servidor:

```powershell
.\mvnw.cmd -DskipTests package
```

## Observações importantes

- o backend habilita CORS para o frontend em `http://localhost:4200`;
- se o banco estiver vazio, a aplicação popula dados de exemplo automaticamente;
- o projeto assume integração com um ambiente real do Mercado Pago; em desenvolvimento, use credenciais de sandbox;
- a url de webhook deve estar acessível externamente pela internet;
- nunca compartilhe ou versionar tokens, senhas ou segredos do Mercado Pago.

## Licença

Este projeto está licenciado sob a licença Apache 2.0. Consulte o arquivo `LICENSE` para mais detalhes.
