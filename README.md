# 🏧 Java Masters Bank — Caixa Eletrônico

> Projeto da disciplina de **Programação Orientada a Objetos** — Java
> Grupo: **Java Masters** · Entrega: 04 a 08 de maio de 2026

---

## 📋 Sobre o Projeto

Simulação completa de um caixa eletrônico com interface gráfica Swing.
O sistema gerencia 6 tipos de cédulas **(R$ 2, 5, 10, 20, 50 e 100)** e implementa todas as operações exigidas pelo professor através da interface `ICaixaEletronico`.

---

## 🗂️ Estrutura do Projeto

```
caixa-eletronico-poo/
├── src/
│   ├── ICaixaEletronico.java   # Interface (contrato) — NÃO modificada
│   ├── CaixaEletronico.java    # Lógica de negócio — implementa o contrato
│   └── GUI.java                # Interface gráfica Swing — Java Masters Bank
└── README.md
```

---

## ✅ Funcionalidades Implementadas

| Botão | Método chamado | Comportamento |
|---|---|---|
| Efetuar Saque | `sacar(Integer valor)` | Algoritmo greedy + backtracking recursivo, máx. 30 cédulas |
| Relatório Cédulas | `pegaRelatorioCedulas()` | Exibe estoque atual de cada nota |
| Valor Total Disponível | `pegaValorTotalDisponivel()` | Soma valor × quantidade de todas as notas |
| Reposição de Cédulas | `reposicaoCedulas(cedula, qtd)` | Adiciona notas ao estoque |
| Cota Mínima | `armazenaCotaMinima(minimo)` | Define saldo mínimo para operação |
| Sair | `gerarExtrato()` | Exibe extrato completo da sessão |

---

## 🧠 Regras de Negócio

- Saque sempre usa as **maiores notas possíveis** (algoritmo greedy)
- Quando o greedy não consegue fechar o valor, um **método recursivo** (`resolverSaque`) encontra a combinação correta — resolve casos como R$8, R$11, etc.
- Máximo de **30 cédulas** emitidas por saque
- Se saldo ficar **abaixo da cota mínima** → `"Caixa Vazio: Chame o Operador"`
- Se o saque **reduziria o saldo abaixo da cota mínima** → saque bloqueado preventivamente
- Se não for possível compor o valor → `"Saque não realizado por falta de cédulas"`
- Cédulas só são descontadas **depois** de confirmar que o saque é 100% possível
- Extrato completo da sessão exibido ao clicar em **Sair**

---

## 📦 Estoque Inicial de Cédulas

| Cédula | Quantidade | Subtotal |
|---|---|---|
| R$ 100,00 | 100 | R$ 10.000,00 |
| R$ 50,00 | 200 | R$ 10.000,00 |
| R$ 20,00 | 300 | R$ 6.000,00 |
| R$ 10,00 | 350 | R$ 3.500,00 |
| R$ 5,00 | 450 | R$ 2.250,00 |
| R$ 2,00 | 500 | R$ 1.000,00 |
| **Total** | **1.900 cédulas** | **R$ 32.750,00** |

---

## 🏛️ Arquitetura POO

```
┌─────────────────────────────────────────┐
│              GUI.java                   │
│  (Interface gráfica — Java Swing)       │
│  Só chama métodos da interface          │
└──────────────────┬──────────────────────┘
                   │ usa
                   ▼
┌─────────────────────────────────────────┐
│         ICaixaEletronico.java           │
│  (Contrato — Interface Java)            │
│  sacar() · pegaRelatorioCedulas()       │
│  pegaValorTotalDisponivel()             │
│  reposicaoCedulas() · armazenaCotas()   │
└──────────────────┬──────────────────────┘
                   │ implementada por
                   ▼
┌─────────────────────────────────────────┐
│        CaixaEletronico.java             │
│  (Lógica de negócio)                    │
│  matriz 6×2 · greedy + recursão         │
│  extrato · cota mínima                  │
└─────────────────────────────────────────┘
```

**Conceitos POO aplicados:**
- **Interface:** contrato que obriga a implementação dos 5 métodos
- **Encapsulamento:** a matriz `cedulas` é `private` — acesso só pelos métodos
- **Separação de responsabilidades:** a GUI nunca calcula, só chama e exibe

---

## 🔢 Como o Algoritmo de Saque Funciona

O saque usa duas estratégias combinadas:

**1. Greedy (guloso)** — tenta sempre a maior nota primeiro:
```
Saque R$170:
→ R$100: usa 1  → restante = R$70
→ R$ 50: usa 1  → restante = R$20
→ R$ 20: usa 1  → restante = R$0  ✔  (3 cédulas)
```

**2. Recursão (backtracking)** — entra quando o greedy não fecha o valor:
```
Saque R$11:
→ Greedy: R$10 + sobra R$1 (falha)
→ Recursão: testa combinações → R$5 + R$2 + R$2 + R$2 = R$11  ✔
```

---

## ▶️ Como Executar

### Via Eclipse (recomendado)
1. **File → New → Java Project** — nome: `caixa-eletronico-poo`
2. Cole os 3 arquivos `.java` na pasta `src`
3. Pressione **F5** para atualizar
4. Clique com botão direito em `CaixaEletronico.java` → **Run As → Java Application**

### Via Terminal
```bash
cd src
javac ICaixaEletronico.java CaixaEletronico.java GUI.java
java CaixaEletronico
```

> **Requisito:** JDK 8 ou superior

---

## 🎨 Design da Interface

Tema **"Dark Banking Premium"** com identidade do grupo:

- Logo circular **"J"** dourado no cabeçalho
- Nome **"Java Masters Bank"** em destaque
- Relógio em tempo real
- Status bar com saldo atualizado após cada operação
- Separação visual por módulo: **Cliente / Administrador / Ambos**
- Extrato em janela estilo recibo bancário ao clicar em Sair

---

## 👥 Integrantes do Grupo

| Nome | RA |
|---|---|
| Bruno Macedo Medrades | 43699596 |
| Fernando Lino lunguinho | 43017231 |
| Giovanna Peixoto severo de Araújo | 42357764 |
| Guilherme de Sousa Santos | 42880386 |
| Jean dos Santos Silva | 43455611 |
| João Vitor Soares | 42216486 |
| Matheus Elies de Oliveira | 47175044 |
| Otávio Pinheiro de Santana | 43670954 |
| Pedro Henrique Pereira Gomes | 43658890 |
| Renan Fernandes Beata Caetano | 42064911 |
| Samira Osman Pechliye | 43409687 |

---

## 📝 Histórico de Commits

```
feat: adiciona interface ICaixaEletronico
feat: implementa algoritmo greedy no metodo sacar
feat: adiciona cota minima e bloqueio de saques
feat: implementa extrato da sessao
feat: adiciona interface grafica GUI Swing
fix: corrige saque de valores como R$8 e R$11 com recursao
docs: adiciona README completo
docs: adiciona integrantes do grupo
```

---

## 📅 Informações da Entrega

| Item | Detalhe |
|---|---|
| Disciplina | Programação Orientada a Objetos |
| Linguagem | Java |
| Prazo | 04 a 08 de maio de 2026 |
| Repositório | GitHub (obrigatório) |
| Apresentação | 2 alunos escolhidos aleatoriamente pelo professor |

---

*Java Masters · POO 2026*
