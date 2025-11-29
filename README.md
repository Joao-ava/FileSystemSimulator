# File System Simulator

Aplicação para simular um sistema de arquivos.

## Introdução ao Sistema de Arquivos com Journaling

TODO: fazer descrição

## Arquitetura do Simulador

![Arquitetura do projeto](./assets/img.png)

No projeto a interatividade é lidada pela classe `CLI`, enquanto as
operações de manipulação do sistema de arquivos e salvar os dados é
feito pelo `FileSystemSimulator` em que ele tem um atributo que
representa a raíz do sistema de arquivos a classe `Directory` representa
um diretório que contem filhos que podem ser outros diretórios ou
arquivos.

## Instalação e funcionamento 

Pelo terminal estando na raiz do projeto (FileSystemSimulator), compile os arquivos com o comando:

```shell
javac -d out src/*.java src/exceptions/*.java
```

Execute o programa com 

```shell
java -cp out Main
```
