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
Entre as dificuldades enfrentadas no projeto, tivemos bugs a solucionar relacionados ao RayCasting (personagens com enquadramento errado ou sendo renderizados na tela mesmo quando não estavam no campo de visão do jogador); alguns bugs na renderização do grid e erros nas convenções adotadas para as coordenadas (cometemos com certa frequência erros relacionados a onde era a origem da matriz e enganos com a inversão das coordenadas i e j); aprender e compreender como a linguagem Kotlin e o LibKTX (LibGDX para o Kotlin) podem ser diferenciais no código.

O projeto, inicialmente, nos parecia bem definido com uma arquitetura relativamente simples que tínhamos em mente (vide slides iniciais). Entretanto, conforme o avanço do projeto, percebemos a necessidade de diversas interfaces (visando o polimorfismo e o encapsulamento) e design patterns (adapter e strategy) que foram essenciais para formar um software organizado, consistente com o paradigma da orientação a objetos e facilmente expansível. Isso pode ser observado comparando os diagramas inicial e final, em que se observa a presença de diversas interfaces que comunicam entre si e entre os componentes. Não apenas na arquitetura, houve mudanças também quanto nas mecânicas e ideias do jogo, por exemplo: inicialmente, pensávamos que todos os personagens (os robôs) seriam todos iguais e dependeriam de itens para realizar ações como quebrar teias e ovos. Porém, visando a prática da orientação à objetos, decidimos programar 3 classes diferentes (Recon, Botanist e Pyro) que herdam Player, uma classe abstrata.

Por fim, vale constar o esforço despendido em como generalizar as mecânicas do jogo (que, conforme o projeto avançava, foram feitas adaptações conforme a arquitetura era estruturada), como por exemplo: o movimento (foi decidido programar o algoritmo de grafo A*, que pode ser utilizado para mapas de grandes proporções); dispersão dos fungos e a probabilidade de tal ocorrer (programamos a chance de 10% de dispersão para cada um dos tiles adjacentes que sejam paredes, mas tivemos que ficar bem atentos ao fato de que há a possibilidade de dispersar para uma parede inatingível pelo jogador, e portanto não é contabilizada pela variável contadora de fungos), entre outras. Assim, acreditamos que o o resultado final do projeto é facilmente escalável sem perdas de eficiência ou causar bugs.


# Diagramas

## Diagrama Geral da Arquitetura do Jogo
![Diagrama Componentes](assets/readmeAssets/DiagramaGeral.png)
> Primeiramente o GameBuilder irá montar uma Ship, que conterá todas as informações referentes aos tiles, componentes e players. Essas informações serão comunicadas para o view através de interfaces "Drawable"
> para que este apresente-as ao usuário. O input processor é responsável por receber os comandos do jogador e enviar para o selected player que realizará as ações correspondentes. Além disso há um Ray Caster 
> que é responsável por criar a visão 3D e enviar as meshes geradas para o view.

# Destaques de Código
## A Star
~~~kotlin
private fun findPathAstar(source: IVector2, dest: IVector2, acceptBlockedDest: Boolean): List<IVector2>?{
        // ver se a posicao destino eh valida
        if (dest.isOutOfRange() || source.isOutOfRange() || (!acceptBlockedDest && dest.isBlocked())) return null

        {...}
        
        //checar caminho enquanto a mutable list open nao esta vazio e nao chegamos no tile final
        //caso encontrarmos o tile final (node == end) , devolvemos um mutable list com os tiles a serem caminhados
        while (open.isNotEmpty()){
            val node = open.nextNode()
            if (node == end) {
                return resultPath(node)
                caso encontrarmos o tile final, devolvemos um 
            }

            open.remove(node)
            node.wasVisited = true

            node.forEachNeighbor { neighbor ->
                // se eh parede ou visinho possuir componente bloqueador, return
                if (!foundEnd) foundEnd = neighbor == end && acceptBlockedDest
                if ((neighbor != end && neighbor.notTraversable) || neighbor.wasVisited || foundEnd) return@forEachNeighbor

                if (neighbor.parent == null || neighbor.parent?.let {it.g > node.g} == true){
                    neighbor.parent = node
                    neighbor.g = node.g + 1
                    open.add(neighbor)
                    changed.add(node)
                }
                if (neighbor.h == Int.MAX_VALUE){
                    neighbor.h = abs(neighbor.i - end.i) + abs(neighbor.j - end.j)
                }
            }
            if (foundEnd) return resultPath(node)
        }
        //se nao houve return no while, é impossível chegar no tile clicado, logo retornamos um mutable list nulo
        return null
    }
~~~

# Destaques de Orientação A Objetos

# Destaques de Pattern

## Adapter
![Hierarquia Exceções](assets/readmeAssets/adapter.png)

### Código do Pattern
~~~kotlin
class MeshGroup(private val meshes: MutableList<Textured2DMesh>): DrawableMeshGroup {
    ...
    override fun render(camera: Camera, shader: ShaderProgram, initialX: Float, initialY: Float, ratio: Float) {
        shader.bind()
        // manda a matriz da camera para o shader
        ...
        meshes.forEach { mesh ->
            // só troca a textura se a textura anterior for diferente a atual
            if (texture == null || mesh.texture != texture) {
                texture = mesh.texture
                mesh.texture.bind()
            }
            // ajusta as meshes
            mesh.moveAndScale(initialX, initialY, ratio)
            // divide a cor das meshes para ficar mais claro a imagem
            shader.setUniformf("f_colorDiv", mesh.colorDiv)
            mesh.render(shader)
        }
    }
    ...
}
~~~

## Strategy
![Hierarquia Exceções](assets/readmeAssets/strategy.png)

### Código do Pattern
~~~kotlin
class TargetComponent(
    val component: Component,
    val dist: Float
) {
    val type get() = component.type
        ...
    fun die() = component.die()
}
~~~

>  O componente no centro da visão do jogador será responsável por chamar sua própria função "die()" quando o jogador destruí-lo


# Plano de Exceções

## Diagrama da hierarquia de exceções

![Hierarquia Exceções](assets/readmeAssets/exceptions.png)

## Descrição das classes de exceção

| Classe                          | Descrição                                                                                                 |
|---------------------------------|-----------------------------------------------------------------------------------------------------------|
| MapException                    | Engloba todas as exceções de criação de mapa                                                              |
| NumFungiNotSpecifiedException   | Número máximo de fungos não foi especificado no arquivo .map ou é inválido                                |
| NotSquareMapException           | O mapa não é quadrado                                                                                     |
| InvalidCharacterException       | Caractere do mapa é inválido                                                                              |
| InvalidCharacterInEdgeException | Caractere na borda do mapa é inválido, todos os caracteres da borda do mapa devem ser paredes ou fungos   |
| InvalidTextureVertices          | Número de vértices fornecidos para Textured2DMesh é inválido para formação da imagem no formato requerido |

## Agradecimentos
* Ana Luisa Holthausen de Carvalho (arte do jogo)
