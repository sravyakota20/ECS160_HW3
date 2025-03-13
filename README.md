# Sravya Kota (03/12)
# ECS160-HW3: Moderation Microservice

## Overview
This project implements the **Moderation Microservice** for ECS160-HW3. It processes social media posts by checking them against a list of banned words. If a post contains any banned words, it is marked as `FAILED`. Otherwise, it is forwarded to the next microservice for hashtagging.

## Project Structure
This repository consists of:
- **Moderation Service** - REST API service that filters posts based on banned words.
- **JUnit Tests** - Validates moderation logic.
- The parent package is set to **com.ecs160.hw2**

