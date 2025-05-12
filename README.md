# TP1

## Tests

### REST

- Users Service (Gestão de Utilizadores)
  - [x] Criar um utilizador
  - [x] Obter informação de um utilizador
  - [x] Modificar um utilizador;
  - [x] Remover um utilizador;
  - [x] Pesquisar utilizadores;
  - [ ] Criar avatar do utilizador;
  - [ ] Obter avatar do utilizador;
  - [ ] Atualizar avatar do utilizador.
- Content Service (Gestão de Conteúdos do Fórum)
  - [ ] Criar nova entrada/resposta;
  - [ ] Obter entradas de topo;
  - [ ] Criar resposta a uma entrada;
  - [ ] Obter respostas de uma entrada;
  - [ ] Obter conteúdo de uma entrada;
  - [ ] Remover entrada/resposta;
  - [ ] Adicionar/remover up-vote e down-vote;
  - [ ] Operações opcionais: contagem de votos, entradas mais votadas, leitura bloqueante para novas respostas.
- Image Service (Gestão de Imagens)
  - [x] Criar imagens;
  - [x] Devolver imagens;
  - [x] Remover imagens.
- [x] Implementar Discovery.

### GRPC

- Users Service (Gestão de Utilizadores)
  - [ ] Criar um utilizador
  - [ ] Obter informação de um utilizador
  - [ ] Modificar um utilizador;
  - [ ] Remover um utilizador;
  - [ ] Pesquisar utilizadores.
- Content Service (Gestão de Conteúdos do Fórum)
  - [ ] Criar nova entrada/resposta;
  - [ ] Obter entradas de topo;
  - [ ] Criar resposta a uma entrada;
  - [ ] Obter respostas de uma entrada;
  - [ ] Obter conteúdo de uma entrada;
  - [ ] Remover entrada/resposta;
  - [ ] Adicionar/remover up-vote e down-vote;
  - [ ] Operações opcionais: contagem de votos, entradas mais votadas, leitura bloqueante para novas respostas.
- Image Service (Gestão de Imagens)
  - [ ]Armazenar e fornecer avatares e imagens associadas aos conteúdos;
  - [ ]Remover imagens quando o conteúdo for apagado.

# TP2
## Objetivo Principal: 

Tornar o o sistema mais seguro:

- [ ] Usar TLS nos servidores e clientes (certificar que estamos a falar com o servidor correto em segurança);
- [ ] Criar novo Image Service remoto (Imgur);
- [ ] Aguentar problemas de integridade entre coteúdo guardado no Image Service e referenciado no Content Service;
- [ ] Certificar que o Content Service é à prova de falhas (suport up to 1 crash failure).