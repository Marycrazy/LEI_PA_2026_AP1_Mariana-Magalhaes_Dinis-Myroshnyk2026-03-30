# Setup — SurrealDB Java Driver

Este projeto utiliza o SurrealDB Java Driver, que devido ao seu tamanho (≈249 MB) não está incluído no repositório. Segue os passos abaixo para o instalar manualmente.
## Pré-requisitos
- Java 8 ou superior
## Instalação do Driver

1. Aceda ao link de download direto e baixar a versão 2.0.0 do driver: https://mvnrepository.com/artifact/com.surrealdb/surrealdb
   > Ou se preferir pode visitar a documentação oficial, referente á linguagem java: https://surrealdb.com/docs/languages/java/installation
2. Coloca o ficheiro .jar descarregado dentro da pasta lib/ do projeto.
```
projeto/
   ├── lib/
   │   └── surrealdb-2.0.0.jar   ← colocar aqui
   ├── src/
   └── ...
```
