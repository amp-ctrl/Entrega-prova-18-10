# Sistema Escolar — Spring Boot

Projeto base da atividade **Caça aos Erros** (Programação Web 1 · Senac Rio).

API REST de um sistema escolar de cadastro com **Professor**, **Aluno** e **Matrícula**,
usando Java 17, Spring Boot, Spring Data JPA e banco H2 em memória.

## Como executar

```bash
./mvnw spring-boot:run
```

Com o servidor rodando, teste os endpoints no Insomnia ou no Postman
(`/professores`, `/alunos` e `/matriculas`) e acesse o console do banco em
`http://localhost:8080/h2-console`.

Erro 1: Banco de dados não iniciava
Arquivo: pom.xml
O que estava errado: A dependência do H2 estava com <scope>test</scope>, então o banco não subia junto com a aplicação principal.
Como descobri: Logo ao tentar rodar o projeto pela primeira vez, o console estourou ClassNotFoundException reclamando que não achava o driver do org.h2.
Correção: Mudei o escopo da dependência para <scope>runtime</scope>.

Erro 2: Tabelas não eram criadas no H2
Arquivo: application-test.properties
O que estava errado: O spring.jpa.hibernate.ddl-auto estava como none. Como o H2 rodava em memória, as tabelas não existiam quando a aplicação subia.
Como descobri: Fui testar os endpoints no Insomnia e tomava erro 500 dizendo Table "PROFESSORES" not found.
Correção: Troquei o valor de none para update, assim o Hibernate cria e atualiza as tabelas automaticamente.

Erro 3: Rota com nome errado
Arquivo: ProfessorController.java
O que estava errado: O @RequestMapping estava no singular (/professor), quebrando o padrão REST no plural usado no resto do projeto.
Como descobri: Dava erro 404 (Not Found) sempre que eu tentava acessar /professores.
Correção: Mudei a anotação para @RequestMapping("/professores").

Erro 4: ID do professor não gerava sozinho
Arquivo: Professor.java
O que estava errado: O atributo id estava só com @Id, esqueceram de avisar o banco para gerar o valor sozinho.
Como descobri: Ao tentar cadastrar um professor (POST), o Hibernate dava a exceção IdentifierGenerationException, reclamando que o ID estava nulo.
Correção: Adicionei a anotação @GeneratedValue(strategy = GenerationType.IDENTITY) no id.

Erro 5: RA com o nome do curso
Arquivo: Aluno.java
O que estava errado: No construtor que recebe os dados do DTO, o campo this.ra estava recebendo dados.curso() por engano.
Como descobri: Cadastrei um aluno e, quando fui listar, o RA dele aparecia escrito "Engenharia de Software" em vez do número do registro.
Correção: Corrigi a linha no construtor para this.ra = dados.ra();.

Erro 6: O PUT não salvava no banco
Arquivo: AlunoController.java
O que estava errado: O método de atualizar dados estava sem a anotação @Transactional. Sem isso, o Hibernate não comita as mudanças no banco de dados.
Como descobri: Dava o PUT com os dados novos, retornava 200, mas quando eu dava um GET, as informações antigas continuavam lá.
Correção: Coloquei o @Transactional no método atualizar.

Erro 7: Aluno só podia ter uma matrícula
Arquivo: Matricula.java
O que estava errado: A relação com o Aluno estava mapeada como @OneToOne, criando uma trava no banco que impedia mais de uma matrícula por aluno.
Como descobri: Tentei cadastrar uma segunda matrícula para um mesmo aluno e o banco estourou erro de violação de chave única (Unique index or primary key violation).
Correção: Mudei o mapeamento de @OneToOne para @ManyToOne.

Erro 8: Nome do campo errado no JSON
Arquivo: DadosCadastroMatricula.java e MatriculaController.java
O que estava errado: O DTO esperava o nome professorId, mas o JSON de teste padrão mandava idProfessor. Isso fazia o valor chegar nulo no Controller.
Como descobri: Mandava o POST de matrícula e tomava erro 500 dizendo The given id must not be null na hora de buscar o professor no repositório.
Correção: Adicionei um @JsonAlias("professorId") no DTO para ele aceitar os dois formatos de nome e ajustei o Controller para usar a propriedade correta.