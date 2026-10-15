# Library Management CI/CD Project

A beginner-friendly Java + Maven + Selenium + Docker + Jenkins project for an end-to-end CI/CD lab.

## What this project does

- Displays a Library Management web page.
- Lets a user add a book title and author.
- Displays added books in a table.
- Includes a unit test for the book service.
- Includes Selenium browser tests for adding a book.
- Builds a Docker image.
- Uses Jenkins to build, test, package, build the image, start the app and run Selenium tests in a containerized Chrome browser.

> Demo note: book data is stored in memory, so it resets when the application restarts. This is intentional to keep the lab project easy to understand.

## Requirements

For local development:
- JDK 17 or newer (the project is configured for Java 17)
- Maven 3.9+
- Git
- Docker Desktop (with Docker Compose)
- Jenkins with permission to run Docker commands, if running the pipeline in Jenkins

For Selenium tests, Docker Compose starts a Selenium Chrome container. You do not need to install Chrome manually for the Docker-based test flow.

## Project structure

```text
library-management-cicd/
├── src/
│   ├── main/
│   │   ├── java/com/example/library/
│   │   │   ├── LibraryManagementApplication.java
│   │   │   ├── Book.java
│   │   │   ├── BookService.java
│   │   │   └── LibraryController.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           └── style.css
│   └── test/java/com/example/library/
│       ├── BookServiceTest.java
│       └── LibrarySeleniumIT.java
├── Dockerfile
├── docker-compose.yml
├── Jenkinsfile
├── pom.xml
└── .gitignore
```

## 1. Run the unit tests

Open a terminal in the project directory:

```bash
mvn clean test
```

This runs the fast unit tests. The Selenium browser test is named `*IT.java` and is run in the Docker-based integration-test step below.

## 2. Run the web application locally

```bash
mvn spring-boot:run
```

Open `http://localhost:8080`.

Enter a book title and author, then select **Add Book**.

Stop the application with `Ctrl+C`.

## 3. Build and run with Docker

Build the image:

```bash
docker build -t library-management-cicd:latest .
```

Run it:

```bash
docker run --rm -p 8080:8080 library-management-cicd:latest
```

Open `http://localhost:8080`.

## 4. Run Selenium tests using Docker Compose

Make sure Docker Desktop is running. From the project root:

```bash
docker compose up -d app selenium
docker compose run --rm test-runner
docker compose down
```

The `test-runner` container runs Maven integration tests against the app using the Selenium Chrome container. Test reports are created under `target/surefire-reports` and `target/failsafe-reports` in the project folder.

If the app takes a little time to start, the Selenium test retries opening the page for a short period.

## 5. Push this project to GitHub

1. Create a new empty repository on GitHub, for example `library-management-cicd`. Do not add a README if you already have this one.
2. Open a terminal in this project folder.
3. Run:

```bash
git init
git add .
git commit -m "Initial Library Management CI/CD project"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/library-management-cicd.git
git push -u origin main
```

Replace `YOUR-USERNAME` with your GitHub username.

## 6. Configure Jenkins

### Prerequisites

- Jenkins is installed and running.
- Git, Maven, and Docker Desktop/Engine are installed and available to the Jenkins agent.
- The Jenkins agent user can run `docker` and `docker compose` commands.
- The project has been pushed to GitHub.
- The Jenkinsfile supports Unix-like agents (`sh`) and Windows agents (`bat`); Docker Compose must be available on either agent.

### Create the Pipeline job

1. Open Jenkins.
2. Select **New Item**.
3. Enter `Library-Management-CICD`.
4. Select **Pipeline** and click **OK**.
5. Under **Pipeline**, set **Definition** to **Pipeline script from SCM**.
6. Select **Git**.
7. Enter your GitHub repository URL.
8. Set branch to `*/main`.
9. Set **Script Path** to `Jenkinsfile`.
10. Save and click **Build Now**.

### Pipeline stages

1. Checkout source code
2. Compile and run unit tests
3. Package the application
4. Build the Docker image
5. Start the application and Selenium Chrome service
6. Run Selenium browser integration tests
7. Stop test containers and archive test reports

### Jenkins and Docker note

The Jenkins process must have access to the Docker daemon. On Windows, the easiest lab setup is often Jenkins running on the same machine where Docker Desktop is running, with Docker CLI/Compose available to the Jenkins agent. A Jenkins container needs an explicitly configured Docker socket/daemon connection; merely installing the Docker plugin does not automatically provide Docker access.

## 7. Optional: publish to Docker Hub

The provided pipeline builds a local image named `library-management-cicd:latest`. To push to Docker Hub, add Jenkins credentials and a `docker login`/`docker push` stage. Never put Docker Hub passwords or access tokens directly in `Jenkinsfile`.

## Troubleshooting

- **Port 8080 is already in use:** stop the other app/container or change the host-side port in `docker-compose.yml`.
- **Docker command fails in Jenkins:** verify Docker CLI/Compose are installed and the Jenkins agent can access the Docker daemon.
- **Selenium cannot reach the app:** use the Compose service name `http://app:8080`, not `localhost`, from inside containers.
- **Maven cannot download dependencies:** check internet/proxy access and retry `mvn clean test`.
- **Java version errors:** check `java -version` and `mvn -version`; both should point to a compatible Java 17+ installation.

## Lab viva summary

- **Git/GitHub:** version control and source-code hosting.
- **Maven:** dependency management, compile, test and package.
- **Selenium:** automates browser interactions and validates the web page.
- **Docker:** packages the application into a portable image.
- **Jenkins:** automates the pipeline stages after a code change.
- **CI/CD:** repeatable integration, testing, packaging and delivery workflow.
