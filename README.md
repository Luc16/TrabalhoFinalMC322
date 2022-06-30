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

# Diagramas

## Diagrama Geral da Arquitetura do Jogo
![Diagrama Componentes](assets/readmeAssets/DiagramaGeral.png)
> Primeiramente o GameBuilder irá montar uma Ship, que conterá todas as informações referentes aos tiles, componentes e players. Essas informações serão comunicadas para o view através de interfaces "Drawable"
> para que este apresente-as ao usuário. O input processor é responsável por receber os comandos do jogador e enviar para o selected player que realizará as ações correspondentes. Além disso há um Ray Caster 
> que é responsável por criar a visão 3D e enviar as meshes geradas para o view.

## Agradecimentos
* Ana Luisa Holthausen de Carvalho (arte do jogo)