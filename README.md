# Setup - Dependências

Este projeto utiliza várias bibliotecas externas que, devido ao seu tamanho, não estão incluídas no repositório. Segue os passos abaixo para as instalar manualmente.

## Pré-requisitos
- Java 8 ou superior

## Instalação das Dependências

Todas as bibliotecas devem ser colocadas dentro da pasta `lib/` do projeto.

### 1. SurrealDB Java Driver (v2.0.0)
- Download: https://mvnrepository.com/artifact/com.surrealdb/surrealdb
- Documentação: https://surrealdb.com/docs/languages/java/installation

### 2. Jakarta Mail (v2.0.2)
- Download: https://mvnrepository.com/artifact/com.sun.mail/jakarta.mail/2.0.2

### 3. Jakarta Activation (v2.0.1)
- Download: https://mvnrepository.com/artifact/com.sun.activation/jakarta.activation/2.0.1

### 4. Apache PDFBox (v3.0.7)
- Download: https://pdfbox.apache.org/download.html

## Estrutura esperada

Após descarregar todos os ficheiros, a estrutura da pasta `lib/` deve ser a seguinte:

projeto/
   ├── lib/
   │   ├── surrealdb-2.0.0.jar
   │   ├── jakarta.mail-2.0.2.jar
   │   ├── jakarta.activation-2.0.1.jar
   │   └── pdfbox-app-3.0.7.jar
   ├── src/
   └── ...

## Executar a aplicação

O ficheiro JAR da aplicação deve ser colocado na mesma diretoria que a pasta `lib/` e executado com o seguinte comando:
java -jar App.jar

> O ficheiro `config.properties` com as configurações de acesso à base de dados, email e serviço de imagens encontra-se disponível em anexo no relatório. Deve ser colocado na pasta `db/` antes de executar a aplicação.