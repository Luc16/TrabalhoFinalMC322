# Projeto Alien Nest Cleaners

# Descrição Resumida do Projeto/Jogo

Projeto da disciplina de orientação a objetos, primeiro semestre de 2022.
Nesse jogo três robos controlados remotamente são encarregados de eliminar os ovos de um alien invasor para que este saia da nave.
Além disso, é necessário controlar a quantidade de fungos que vem crescendo na nave e, principalmente, manter os robôs a salvo do alien que espreita na escuridão.

# Equipe
* `Luc Joffily Ribas` - `247231` 
* `Rafael Gregori de Souza` - `247346`

# Arquivo Executável do Jogo

> [Link para o executável](assets/readmeAssets/ANC.jar)

# Slides do Projeto

## Slides da Prévia
>[Link](https://drive.google.com/file/d/1b1BZOB1QTkyP8iMLsTfIILZwD4t5lt8q/view?usp=sharing)

## Slides da Apresentação Final
> [Link](https://docs.google.com/presentation/d/1xyjWFhrCbyZV30Mo_N_7FFh13gUgEdvw8kLYVz-yiSo/edit?usp=sharing)

## Relatório de Evolução
Entre as dificuldades enfrentadas no projeto, tivemos bugs a solucionar relacionados ao RayCasting (personagens com enquadramento errado ou sendo renderizados na tela mesmo quando não estavam no campo de visão do jogador); alguns bugs na renderização do grid e erros nas convenções adotadas para as coordenadas (cometemos com certa frequência erros relacionados a onde era a origem da matriz e enganos com a inversão das coordenadas i e j); aprender e compreender como a linguagem Kotlin pode ser um diferencial no código.

O projeto, inicialmente, nos parecia bem definido com uma arquitetura relativamente simples que tínhamos em mente (vide slides iniciais). Entretanto, conforme o avanço do projeto, percebemos a necessidade de diversas interfaces (visando o polimorfismo e o encapsulamento) e design patterns (adapter e strategy) que foram essenciais para formar um software organizado, consistente com o paradigma da orientação a objetos e facilmente expansível. Isso pode ser observado comparando os diagramas inicial e final, em que se observa a presença de diversas interfaces que comunicam entre si e entre os componentes e gene. Não apenas na arquitetura, houve mudanças também quanto nas mecânicas e ideias do jogo, por exemplo: inicialmente, pensávamos que todos os personagens (os robôs) seriam todos iguais e dependeriam de itens para realizar ações como quebrar teias e ovos. Porém, visando a prática da orientação à objetos, decidimos programar 3 classes diferentes (Recon, Botanist e Pyro) que herdam Player, uma classe abstrata.




# Diagramas

## Diagrama Geral da Arquitetura do Jogo
![Diagrama Componentes](assets/readmeAssets/DiagramaGeral.png)
> Primeiramente o GameBuilder irá montar uma Ship, que conterá todas as informações referentes aos tiles, componentes e players. Essas informações serão comunicadas para o view através de interfaces "Drawable"
> para que este apresente-as ao usuário. O input processor é responsável por receber os comandos do jogador e enviar para o selected player que realizará as ações correspondentes. Além disso há um Ray Caster 
> que é responsável por criar a visão 3D e enviar as meshes geradas para o view.

## Agradecimentos
* Ana Luisa Holthausen de Carvalho (arte do jogo)
