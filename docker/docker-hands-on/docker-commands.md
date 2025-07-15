## Let's run something on Docker

```bash
docker pull ram1uj/spring-boot
docker run -p 8000:5000 -d --name spring-boot ram1uj/spring-boot
```

## Easy-recipes app

```bash
docker pull ram1uj/easy-recipes
docker run -p 8000:8080 -d --name easy-recipes ram1uj/easy-recipes
```
