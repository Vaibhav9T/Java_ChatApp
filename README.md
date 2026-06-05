# 💬 Real-Time AI Chat Platform

A high-performance, full-stack chat application that blends traditional real-time messaging with modern generative AI. Users can join dedicated chat rooms, exchange real-time messages, and invoke an integrated AI Assistant (`@AI`) directly within the chat stream.

## ✨ Key Features

* **Real-Time WebSockets:** Low-latency, bidirectional communication powered by Spring WebSockets and STOMP protocols.
* **Integrated AI Agent:** Mentions of `@AI` are intercepted by the backend and processed asynchronously via the Google Gemini 2.5 Flash API, injecting intelligent responses directly into the chat room.
* **Non-Blocking Architecture:** AI requests are handled on separate background threads to ensure the chat room remains lightning-fast and completely unblocked during API calls.
* **Modern UI/UX:** A responsive, sleek interface built with Next.js, React, and Tailwind CSS, featuring custom styling to distinguish AI messages from human users.
* **Relational Persistence:** Robust data storage using PostgreSQL and Hibernate/JPA to persist users, chat rooms, and message histories.
* **Secure Configuration:** Enterprise-grade environment variable management ensuring API keys and database credentials are fully isolated.

## 🛠️ Tech Stack

**Frontend:**
* Next.js (React framework)
* TypeScript
* Tailwind CSS
* SockJS & STOMP.js (WebSocket clients)

**Backend:**
* Java 21
* Spring Boot (Web, WebSockets, Data JPA)
* PostgreSQL (Database)
* Google Gemini API (Generative AI)
* Spring Dotenv (Environment management)

## 🚀 Architecture Overview

The application utilizes a publish-subscribe (Pub/Sub) pattern via WebSockets. 
1. The **Next.js client** subscribes to a specific chat room topic (e.g., `/topic/{roomId}`).
2. The **Spring Boot Controller** intercepts incoming messages.
3. If a message begins with `@AI`, the `AiService` is triggered. It spawns a background thread, securely communicates with the Gemini API, and uses a `SimpMessagingTemplate` to broadcast the AI's response back to the specific WebSocket topic, simulating a live user.

## 💻 Getting Started (Local Development)

### Prerequisites
* Java 21+
* Node.js 18+
* PostgreSQL installed and running locally
* A valid Google Gemini API Key

### 1. Clone the Repository
```bash
git clone [https://github.com/Vaibhav9T/Java_ChatApp.git](https://github.com/Vaibhav9T/Java_ChatApp.git)
cd Java_ChatApp
