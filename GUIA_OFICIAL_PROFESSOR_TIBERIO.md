# ☕ TechLab Inventory & Service Desk — Guia Oficial do Semestre

> **Disciplina:** Programação Orientada a Objetos & Persistência (2026.2)  
> **Professor:** Me. Tibério Bezerra Soares  
> **Instituição:** Faculdade SENAC Ceará  
> **Fonte Oficial:** [github.com/tbezerrasoares/POO_e_Persistencia_2026.2](https://github.com/tbezerrasoares/POO_e_Persistencia_2026.2)

---

## 📅 Calendário Semestral do Projeto

| Semana | Período (Semana de) | Etapa / Conteúdo | Conteúdo & Evolução no Projeto (TechLab Inventory) | Entregável no GitHub |
| :---: | :---: | :--- | :--- | :--- |
| **01** | 10/Ago – 14/Ago | Aula 01 | Setup & Git/GitHub: Introdução à POO e fluxo Git em máquinas compartilhadas. | Repositório `techlab-inventory` com `README.md`. |
| **02** | 17/Ago – 21/Ago | Aula 02 | Modelagem Inicial: Classes, Objetos, Encapsulamento (Getters/Setters). | Classes `AtivoTI` e `ChamadoSuporte` criadas. |
| **03** | 24/Ago – 28/Ago | Aula 03 | Ciclo de Vida & Memória: Alocação de memória, referências e passagem por valor/referência. | Métodos para vinculação direta de objetos na memória RAM. |
| **04** | 31/Ago – 04/Set | Aula 04 | Padronização: Sobrecarga de construtores/métodos e representação em texto. | Construtores flexíveis e formatação para exibição. |
| **05** | 07/Set – 11/Set | Aula 05 | Herança & Especialização: Subclasses de ativos (`Servidor`, `EstacaoTrabalho`). | Hierarquia de ativos implementada. |
| **06** | 14/Set – 18/Set | Aula 06 | Abstração & Interfaces: Contratos de software e desacoplamento (`INotificavel`, `IPersistencia`). | Interfaces do sistema definidas. |
| **07** | 21/Set – 25/Set | Aula 07 | Tratamento de Exceções: Robustez do sistema com `try/catch` e exceções personalizadas. | Lançamento e captura de exceções nos métodos. |
| **08** | 28/Set – 02/Out | Revisão / Mentoria | Revisão Geral do Módulo em Memória: Suporte individualizado aos alunos para alinhar os códigos e os repositórios. | Limpeza de bugs e push consolidado no GitHub. |
| **09** | 05/Out – 09/Out | AVALIAÇÃO 1 | 1ª Avaliação Prática / Entrega Parcial: Validação da POO completa em memória (Herança, Interfaces, Exceções). | Release `v0.5-alpha` taguado no GitHub. |
| **10** | 12/Out – 16/Out | Recesso / Ajuste | Semana de Ajuste do Calendário / Recesso escolar. | Tempo de consolidação para os alunos. |
| **11** | 19/Out – 23/Out | Aula 08 | Mapeamento de Dados & Casts: Conversão de tipos, casting seguro e Data Transfer Objects (DTO). | Mapeadores de objetos e conversão de tipos. |
| **12** | 26/Out – 30/Out | Aula 09 | Coleções & Genéricos: Gerenciamento de listas de dados, iteradores, buscas e filtros em memória. | Gerenciador de ativos/chamados via Coleções/Listas. |
| **13** | 02/Nov – 06/Nov | Aula 10 | Persistência I (Arquivos): Escrita e leitura de dados em arquivos `.txt`, `.csv` ou `.json`. | Módulo de leitura/gravação em arquivo local. |
| **14** | 09/Nov – 13/Nov | **Aula 11 (HOJE)** | **Persistência II (Banco de Dados): Conexão com BD real (SQLite/MySQL), Padrão DAO/Repository e SQL Injection.** | **AtivoRepository / AtivoDAO funcional com CRUD.** |
| **15** | 16/Nov – 20/Nov | Integração do BD | Integração BD + Interface: Conexão final do projeto com a base de dados. | CRUD completo e testado na aplicação. |
| **16** | 23/Nov – 27/Nov | Polimento e Testes | Homologação Final: Refatoração de código, testes de borda e documentação final do README.md. | Código-fonte congelado no GitHub. |
| **17** | 30/Nov – 04/Dez | AVALIAÇÃO 2 | 2ª Avaliação (Apresentação Final / Exame): Defesa prática do projeto TechLab Inventory rodando com Banco de Dados. | Release `v1.0-final` taguado e encerramento. |

---

## 🎯 Requisitos de Domínio Estabelecidos pelo Professor

1. **Classe Base Abstrata (`AtivoTI`):**  
   Atributos privados/protegidos: `id`, `codigoPatrimonio` (ou `patrimonio`), `modelo`, `dataAquisicao` e `status` (`Ativo`, `EmManutencao`, `Descartado`), utilizando encapsulamento com getters e setters e o método abstrato `calcularCustoManutencaoMensal()`.

2. **Especialização de Ativos (Herança):**
   - **`Servidor`:** `ipEstatico`, `sistemaOperacional`, `capacidadeRAM` (ou `memoriaRam`).
   - **`EstacaoTrabalho`:** `departamento`, `usuarioAlocado`.
   - **`DispositivoRede`:** `quantidadePortas`, `tipoEquipamento` (Roteador/Switch).

3. **Gestão de Chamados (`ChamadoSuporte`):**  
   Atributos: `id`, `titulo`, `descricao`, `prioridade` (`Baixa`, `Media`, `Alta`), `dataAbertura` e a associação direta com a referência do `ativoRelacionado` (`AtivoTI`).

4. **Contratos de Software (Interfaces):**
   - `INotificavel`: método `void enviarAlerta(String mensagem)`.
   - `IConexaoBD`: métodos `Connection conectar()`, `void desconectar()`, `ResultSet executarQuery(String sql)`.
   - `IPersistencia` / `IAtivoDAO`: métodos de CRUD desacoplados (`salvar`, `buscarPorId`, `listarTodos`, `atualizar`, `deletar`).

5. **Tratamento de Exceções Customizadas:**  
   `EquipamentoNaoEncontradoException` e `FalhaConexaoBDException`.

6. **Coleções e Polimorfismo:**  
   `List<AtivoTI>` percorrida por laço `for` chamando polimorficamente `calcularCustoManutencaoMensal()` de cada ativo especializado.

7. **Persistência Relacional (Padrão DAO):**  
   Conexão via JDBC (SQLite / MySQL) com `PreparedStatement` para proteção contra SQL Injection.
