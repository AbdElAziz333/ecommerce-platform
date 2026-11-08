# Ecommerce Web Platform

A simple e-commerce web platform written in spring boot.

# Project Structure

backend -> backend files (spring boot)
frontend -> frontend files (reactjs)
docs -> diagrams (ERD, C4, sequence)

## How To Run

NOTE: instructions ain't fully done yet.

#### Locally

apps runs locally
databases in docker

make sure you have Docker installed (check with docker --version)
clone the project (git clone https://github.com/AbdElAziz333/ecommerce-platform)
cd into the project `cd ecommerce-platform`
first, run the databases with `make docker_run_dbs_local` via docker compose
wait some time to run
second, run the backend with `make run_apps_local` via gradle
wait some time to run

#### Docker

apps runs in docker
databases in docker

make sure you have Docker installed (check with docker --version)


- First, run make docker-build in cmd (builds docker images)
- Second, run make run-prod in cmd and volah