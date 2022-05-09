# ✅👩🏻‍💻 Chack-man backend
Application to automize checking of students semester and exam projects.

## ▶️ Running

The application depends on relational database (PostreSQL is recommended). You can use your local instance, then create new Spring profile under /src/main/resource folder and add dabase credential. Or you can use power of Docker and run container instances using docker compose file in root folder of the project. 

### 🐳 Docker images

1. Download and install docker desktop on your Windows/Mac device. Linux user can use their distro package manager.
2. Run docker compose using command line in the root folder.
3. Wait for the initialization of the conteiners.
4. Run gradle bootRun --args='--spring.profiles.active=docker' (profile is already part of the project).
