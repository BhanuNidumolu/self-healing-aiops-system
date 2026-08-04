<h1 align="center">🚀 Self-Healing AIOps Multi-Agent System</h1>
<h3 align="center">Spring Boot • Multi-Agent Architecture • MCP Tool-Calling • Autonomous Healing</h3>

This project is a complete multi-agent AIOps system that continuously monitors a microservice, analyzes logs & metrics using AI-powered agents, detects anomalies, and triggers automated healing actions — orchestrated by a Supervisor Agent.

It serves as a production-style Proof of Concept (PoC) showing how modern enterprises implement AI-driven self-healing infrastructure.

🧠 System Architecture (High Level)
<p align="center"> <img width="700" src="https://github.com/user-attachments/assets/221921a3-9768-44f5-80b8-abeb5fba9e5c" /> </p>
✔ Fully decoupled microservices
✔ All agents use MCP @Tool functions
✔ Supervisor orchestrates everything
✔ Healing actions performed automatically
⭐ Features


**🟢 1. Metrics Analysis Agent**

Analyzes live system metrics:

CPU usage

Memory usage

Latency

Error counts

AI-generated metric health summary

**🟡 2. Logs Analysis Agent**

Performs intelligent log classification:

Detects:

DB failures

Deadlocks

OOM errors

GC overhead

Slow SQL queries

Cache/Redis issues

Circuit breaker transitions

JWT failures

Network latency problems

Repeated authentication failures

Extracts:

Errors

Warnings

Root Cause

**🔴 3. Anomaly Detection Agent**

Correlates metrics + logs to detect:

Memory leaks

DB connection pool exhaustion

Network instability

CPU/Memory spikes

Latency anomalies

Security problems

**🔧 4. Healing Agent**

Performs autonomous healing:

Restarting microservice

Resetting DB connections

Clearing cache

Scaling actions

**🧠 5. Supervisor Agent**

Calls all agents via MCP tool-calling

Collects & merges all outputs

Performs anomaly assessment

Triggers self-healing

Produces final AIOps incident report

**📡 6. Monitored Service**

Simulates a real microservice by providing:

Random metrics

Realistic logs

Health status
```
🧩 Project Structure
self-healing-aiops-system/
│
├── supervisor-service/
├── metrics-agent/
├── logs-agent/
├── anomaly-agent/
├── healing-agent/
└── monitored-service/
```

Each folder contains its own Spring Boot microservice.

⚙️ How the Multi-Agent Workflow Runs

Supervisor → Metrics Agent

Supervisor → Logs Agent

Supervisor correlates data and sends it to Anomaly Agent

If anomaly detected → Healing Agent is triggered

Supervisor returns Final Combined JSON Report
```
📝 Example Final Output
{
  "metrics": {
    "cpu": 41,
    "memory": 76,
    "latency": 134
  },
  "logs": {
    "errors": [
      "TimeoutException: DB connection failed",
      "OutOfMemoryError: Java heap space"
    ],
    "warnings": [
      "High latency: 540ms",
      "Memory usage high: 82%"
    ],
    "rootCause": "Memory leak / heap exhaustion"
  },
  "anomaly": {
    "anomaly": true,
    "reason": "Critical errors found in logs."
  },
  "healingAction": {
    "action": "restart",
    "status": "SUCCESS",
    "message": "Service restarted successfully."
  },
  "finalStatus": "System healed successfully"
}
```
```
🛠️ Technologies Used
Layer	Tech
Language	Java 21
Framework	Spring Boot 3.x
AI	Spring AI (ChatClient + MCP Tool-Calling)
Protocol	MCP – Model Context Protocol
Build System	Maven
Communication	REST APIs
```
🚀 How to Run All Agents
1️⃣ Start Monitored Service
cd monitored-service
mvn spring-boot:run

2️⃣ Start Metrics Agent
cd metrics-agent
mvn spring-boot:run

3️⃣ Start Logs Agent
cd logs-agent
mvn spring-boot:run

4️⃣ Start Anomaly Agent
cd anomaly-agent
mvn spring-boot:run

5️⃣ Start Healing Agent
cd healing-agent
mvn spring-boot:run

6️⃣ Start Supervisor Service
cd supervisor-service
mvn spring-boot:run

🔍 Testing the System

Visit:

👉 http://localhost:8086/supervise


---

## 🐳 Run the Entire System with Docker

This project supports **one-click multi-agent startup** using Docker Compose — no need to run services manually.

### Prerequisites
- Docker Desktop installed
- Docker Compose enabled
- API keys configured in environment variables (`.env` file or system env)

---

### 🚀 Start All Agents at Once

```bash
docker-compose up --build
Stop the system
bash
Copy code
docker-compose down
Once services start, open in browser:

📌 http://localhost:8086/supervise

This triggers the full AIOps workflow:

Metrics → Logs → Anomaly Detection → Healing → Final Report ✔

Container Overview
Service	Container Port	Purpose
monitored-service	8081 → 8080	Simulated target application
metrics-agent	5173 → 8080	Fetch metrics from monitored service
logs-agent	5174 → 8080	Analyze logs & classify issues
anomaly-agent	5175 → 8080	Detect abnormal behavior
healing-agent	5176 → 8080	Perform recovery actions
supervisor-service	8086 → 8080	Main orchestrator calling all agents

All containers communicate internally over a shared Docker network aiops-net.

🌍 Environment Variables
Create a .env file before running (without committing it to GitHub):

env

API_KEY=your_groq_or_openai_api_key_here


🔥 Advantages of Docker Mode
✔ Zero manual startup — one command boot
✔ Each service runs in its own isolated container
✔ Reproducible environment for demos & deployment
✔ Ready for Kubernetes migration later


---

If you want, I can also generate:

📌 **Docker badges** for README  
📌 **Docker deploy script**  
📌 **Kubernetes YAML next step**  

Just say **"next: kube deployment"** or **"add docker badge
```
<p align="center"> <img width="700" src="https://github.com/user-attachments/assets/3c77cf4d-5816-4d4b-8945-c3e0d5f24adf" /> <img width="700" src="https://github.com/user-attachments/assets/50503e4e-24d2-4c7e-943b-dba6ab6c7100" /> </p>
🧪 Testing Scenarios Supported

✔ Memory leak
✔ DB connection timeout
✔ Deadlocks
✔ Slow SQL queries
✔ High latency
✔ Authentication failure
✔ Circuit breaker OPEN state
✔ Redis timeouts
✔ Network latency issues

💡 All agents collaborate to detect problems, diagnose root cause, and autonomously heal the system.
