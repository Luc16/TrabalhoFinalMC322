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

# Destaques de Código

## A Star
> Este é o algoritmo de busca em grafo A*, utilizado no movimento do Alien e dos jogadores. Essencialmente, este algoritmo realiza um chute pensado de melhor caminho utilizando uma heurística (nesse caso, foi utilizada a Manhattan, a mais adequada para um grid em que os movimentos são cima, baixo, esquerda e direita, ou seja, sem movimentos diagonais), sendo que esse valor será combinado com a distância do nó inicial e teremos, portanto, o valor F que servirá de parâmetro para analisar o próximo nó. Caso haja maior interesse em entender o algoritmo, recomendamos o seguinte vídeo:
> <br> 
[Vídeo](https://www.youtube.com/watch?v=-L-WgKMFuhE&t=181s)
### Código
~~~kotlin
private fun findPathAstar(source: IVector2, dest: IVector2, acceptBlockedDest: Boolean): List<IVector2>?{
        // ver se a posicao destino eh valida
        if (dest.isOutOfRange() || source.isOutOfRange() || (!acceptBlockedDest && dest.isBlocked())) return null
    
        
        //checar caminho enquanto a mutable list open nao esta vazio e nao chegamos no tile final
        //caso encontrarmos o tile final (node == end) , devolvemos um mutable list com os tiles a serem caminhados
        while (open.isNotEmpty()){
            val node = open.nextNode()
            if (node == end) {
                return resultPath(node)
                //caso encontrarmos o tile final, devolvemos um mutableList com os tiles a serem caminhados
            }

            open.remove(node)
            node.wasVisited = true
            
            //para cada um dos vizinhos, calcularemos o valor f = heuristica + distancia_no_inicial e
            // vamos seguir a busca para o no de menor valor f
            node.forEachNeighbor { neighbor ->
                // se eh parede ou vizinho possuir componente bloqueador, return
                if (!foundEnd) foundEnd = neighbor == end && acceptBlockedDest
                if ((neighbor != end && neighbor.notTraversable) || neighbor.wasVisited || foundEnd) return@forEachNeighbor
                
                if (neighbor.parent == null || neighbor.parent?.let {it.g > node.g} == true){
                    neighbor.parent = node
                    neighbor.g = node.g + 1
                    open.add(neighbor)
                    changed.add(node)
                }
                
            }
            if (foundEnd) return resultPath(node)
        }
        //se nao houve return, é impossível chegar no tile clicado, logo retornamos um mutable list nulo
        return null
    }
~~~

# Destaques de Orientação a Objetos

## Sistema de telas:
> Inicialmente, no topo do diagrama, temos a classe abstrata CustomScreen que herda a Screen nativa do LibKTX. Em seguida, temos GameScreen e MenuScreen que são telas dinâmicas (ou seja, com ações), e, no mesmo nível, uma classe abstrata ImageScreen que será herdada por LoseScreen, InstructionScreen e WinScreen, telas que são apenas imagens estáticas.

### Diagrama
![Esquema_Telas](assets/readmeAssets/diagramaTelas.png)

### Código
~~~kotlin
abstract class CustomScreen(
        val game: MyGame,
        val batch: Batch = game.batch,
        val renderer: ShapeRenderer = game.renderer,
        val font: BitmapFont = game.font,
        val viewport: FitViewport = game.gameViewport,
) : KtxScreen {
    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
    ...
}
~~~

## Polimorfismo e Encapsulamento:
> Visando o encapsulamento, vemos na função abaixo que a propriedade tiles utiliza uma matriz de DrawableTile, uma interface 
> que possui apenas as funções draw e drawOutline que são necessárias para desenhar o Grid. Note que a classe Tile implementa
> DrawableTile, de maneira que podemos iterar sobre a matriz de DrawableTile em vez de Tile, aplicando assim o polimorfismo.

### Código
~~~kotlin
private fun drawTileMap(
    ...,
    tiles: List<List<DrawableTile>>,
    ...
    ){
        ...
        renderer.use(ShapeRenderer.ShapeType.Filled, camera.combined){
            ...
            tiles.forEach{ line ->
                line.forEach { tile ->
                    tile.draw(mirroredX, mapRect.y, ratio, renderer)
                }
            }

        }
        ...
        renderer.use(ShapeRenderer.ShapeType.Line, camera.combined){
            tiles.forEach{ line ->
                line.forEach { tile ->
                    tile.drawOutline(mirroredX, mapRect.y, ratio, renderer)
                }
            }
        }
    }
~~~

## Classe abstrata RayCastComponent:
> Todos os RayCastComponents, ou seja, componentes do jogo que serão renderizados em 3D, 
> mas não são paredes, como ovo, jogador e alien, tem certos atributos, como a textura para o 3D
> e a textura para o mapa, e funções, como de renderizar no 3D ou desenhar no mapa

### Código
~~~kotlin
abstract class RayCastComponent(
    tile: RayCastTile,
    ...
    override val texture: Texture,
    val mapTexture: Texture,
):
    Disposable,
    Comparable<RayCastComponent>,
    Component,
    MapBatchDrawable
{
    ...
}
~~~

## Expansibilidade:
> A classe Player é uma classe abstrata que tem todas os atributos e funções para a manipulação e desenho dos jogadores.
> Para criar um novo jogador é muito simples, além de fazer a arte é necessário apenas criar uma nova classe de jogador,
> especificar parâmetros como nome, energia máxima e tamanho da camera de visão e atribuir para o novo jodaor um número 
> no construtor da ship. Para modificar um jogador também é fácil, bastando apenas alterar os atributos na classe.

### Código
~~~kotlin
class NovoJogador(...params) : Player(params) {
    override val name: String
        get() = "Novo Jogador"
    override val maxEnergy: Int
        get() = 10 
    override val webEnergy: Int
        get() = 2
    override val fungusEnergy: Int
        get() = 4
    override val eggEnergy: Int
        get() = 4
    override val cameraPlaneSize: Float
        get() = 0.40f

}
~~~

# Destaques de Pattern

## Adapter
> Como o Raycaster gera várias meshes para renderização e a renderização de cada uma delas individualmente é mais cara e mais trabalhosa do que em grupo, foi implementado um adaptador de lista de mehes (MeshGroup) para que essas meshes pudessem ser renderizadas de maneira mais compartimentalizada e eficiente

### Diagrama do Pattern
![Hierarquia Exceções](assets/readmeAssets/adapter.png)

### Código do Pattern
~~~kotlin
class MeshGroup(private val meshes: MutableList<Textured2DMesh>): DrawableMeshGroup {
    ...
    override fun render(...params) {
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
>  O componente no centro da visão do jogador será responsável por chamar sua própria função "die()" quando o jogador destruí-lo

### Diagrama do Pattern
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

# Conclusão e Trabalhos Futuros
Com este projeto, percebemos, inicialmente, a importância do pensamento em alto nível antes de começar a programar de fato o jogo, ou seja, o quão necessário é ter organização prévia. Com o fato de que o jogo finalizado distanciou-se do planejamento inicial, observamos que, no desenvolvimento de software,
muitas vezes o pensamento algorítmico e de arquitetura será claro apenas durante o desenvolvimento do produto, pois é durante o processo de criação que novas dificuldades surgem e portanto adaptações são necessárias.

Nesta linha de pensamento, concluímos assim que os princípios de orientação a objetos e os design patterns tornam-se evidentes quando é sentida a necessidade deles no código.
Enfim, é ideal que o uso dos patterns e princípios sejam de uso natural e lógico, e não forçados propositalmente. Por fim, vale dizer que, principalmente em sistemas de alta escala (como de empresas Big Tech),
o conhecimento de arquitetura de software é essencial para o bom funcionamento, eficiência e reaproveitamento de projetos já realizados.

Entre trabalhos futuros, podemos citar: a adição de mais jogadores humanos para o jogo, ou seja, que mais pessoas joguem simultaneamente e que cada uma delas possua seus próprios robôs
que não podem ser controladas por outras; a adição de aliens de diferentes tipos, sendo que eles podem possuir habilidades diferentes de colocar ovos (como foi feito no jogo atual); adicionar novos componentes, sejam eles
vantajosos para os jogadores ou não, como, por exemplo, um ácido em certos tiles que aumente a energia despendida para andar sobre eles.

# Documentação de Compoenentes

## Diagrama Geral da Arquitetura do Jogo

![Diagrama Geral](assets/readmeAssets/DiagramaGeral.png)
> Primeiramente o GameBuilder irá montar uma Ship, que conterá todas as informações referentes aos tiles, componentes e players. Essas informações serão comunicadas para o view através de interfaces "Drawable"
> para que este apresente-as ao usuário. O input processor é responsável por receber os comandos do jogador e enviar para o selected player que realizará as ações correspondentes. Além disso há um Ray Caster
> que é responsável por criar a visão 3D e enviar as meshes geradas para o view.

## Diagrama de componentes
![Diagrama Componentes](assets/readmeAssets/components.png)

## Componente `Game Builder`
Game Builder é basicamente onde o jogo será inicializado. Conta com a presença de um
carregador de texturas e um leitor de arquivos para gerar o grid.

![Diagrama GB](assets/readmeAssets/GameBuilder.png)

**Ficha Técnica**

item | detalhamento
----- | -----
Pacote | `trabalhofinal.utils`
Autores | `Luc e Rafael`
Interfaces |

## Componente `Game Model`
Game Model concentra comunica para os outros componentes informações necessárias do grid para o andamento do jogo, como a presença de um
componente em certo tile.

![Diagrama GM](assets/readmeAssets/GameModel.png)

**Ficha Técnica**

item | detalhamento
----- | -----
Pacote | `trabalhofinal.components`
Autores | `Luc e Rafael`
Interfaces | `Component` <br> `ComponentShip`

### Detalhamento das Interfaces


#### Interface `Component`

Interface que permite acesso ao método buildGame para montagem do jogo.

~~~kotlin
interface Component {
    val isWall: Boolean
    val texture: Texture?
    var color: Color
    val type: ComponentType

    fun die() {}
}
~~~

Método | Objetivo
-------| --------
`die` | Elimina um componente de um tile.
`isWall` | Retorna se o componente é uma parede ou não.
`texture` | Retorna a textura de um componente.
`color` | Retorna ou seta a cor associada de um componente.
`type` | Retorna o tipo (presentes no Enum ComponentType) do componente. 


## Componente `ShipRenderer`

Componente responsável pelas renderizações tanto de interface gráfica 2D quanto 3D (relacionada ao RayCast)

![Diagrama SR](assets/readmeAssets/shipRenderer.png)

**Ficha Técnica**

item | detalhamento
----- | -----
Pacote | `trabalhofinal.components`
Autores | `Luc e Rafael`
Interfaces | `DrawableMeshGroup` <br>`DrawableRayCaster` <br> `DrawableShip` <br> `DrawableTile` <br> `MapBatchDrawable` <br> `MapShapeDrawable` 

### Detalhamento das Interfaces

#### Interface `MapShapeDrawable`

Interface que representa um elemento que pode ser desenhado no mapa utilizando o ShapeRenderer

~~~kotlin
interface MapShapeDrawable {
    fun draw(startX: Float, startY: Float, ratio: Float, renderer: ShapeRenderer)
}
~~~

Método | Objetivo
-------| --------
`draw` | Desenha o elemento na tela utilizando o ShapeRenderer.

#### Interface `MapBatchDrawable`

Interface que representa um elemento que pode ser desenhado no mapa utilizando um Batch (com uma textura)

~~~kotlin
interface MapBatchDrawable {
    fun draw(startX: Float, startY: Float, ratio: Float, batch: Batch)
}
~~~

Método | Objetivo
-------| --------
`draw` | Desenha o elemento na tela utilizando o Batch.

#### Interface `DrawableTile`

Interface que implementa MapShapeDrawable, de maneira a representar um tile que tem tanto seu retângulo quanto sua borda desenhados pelo ShapeRenderer

~~~kotlin
interface DrawableTile: MapShapeDrawable {
    fun drawOutline(startX: Float, startY: Float, ratio: Float, renderer: ShapeRenderer)
}
~~~

Método | Objetivo
-------| --------
`drawOutline` | Desenha a borda do tile utilizando o ShapeRenderer.

#### Interface `DrawableMeshGroup`

Interface que representa um MeshGroup que pode ter suas meshes desenhadas utilizando um shader provido.

~~~kotlin
interface DrawableMeshGroup: Disposable {
    fun render(camera: Camera, shader: ShaderProgram, initialX: Float = 0f, initialY: Float = 0f, ratio: Float = 1f)
}
~~~

Método | Objetivo
-------| --------
`render` | Desenha todos os itens de um MeshGroup em 3D utilizando um shader.


#### Interface `DrawableShip`

Interface que representa a Ship a partir do polimorfismo em drawableTiles e possui atributos relevantes para o fim do jogo

~~~kotlin
interface DrawableShip {
    val drawableTiles: List<List<DrawableTile>>
    val components: MapBatchDrawable
    var numFungi: Int
    val maxFungi: Int
    val numEggs: Int

    fun renderComponents(shader: ShaderProgram, initialX: Float = 0f, initialY: Float = 0f, ratio: Float = 1f)

}
~~~


## Componente `Game Control`
Componente responsável pelo controle do jogo, em que as ações recebidas seja por teclado e mouse (PC) quanto por toque (Android)
são comunicadas para o jogador atual (Selected Player)

![Diagrama GC](assets/readmeAssets/GameControl.png)

**Ficha Técnica**

item | detalhamento
----- | -----
Pacote | `trabalhofinal.screens`
Autores | `Luc e Rafael`
Interfaces |

## Componente `RayCaster`
Componente responsável por prover informações para a renderização 3D, sendo que as informações dos tiles a serem processadas são recebidas da Ship através da interface RayCastTile.

![Diagrama RC](assets/readmeAssets/RayCaster.png)

**Ficha Técnica**

item | detalhamento
----- | -----
Pacote | `trabalhofinal.utils`
Autores | `Luc e Rafael`
Interfaces | `RayCastTile`

### Detalhamento das Interfaces


#### Interface `RayCastTile`

Interface que representa um Tile apenas com as informações necessária para o RayCast

~~~kotlin
interface RayCastTile {
    val x: Float
    val y: Float
    var isWall: Boolean
    val texture: Texture?
    var component: Component?
    var i: Int
    var j: Int
}
~~~

Método | Objetivo
-------| --------
`x` | Retorna a posição x real do tile.
`y` | Retorna a posição y real do tile.
`isWall` | Retorna ou seta se o Tile é parede ou não.
`texture` | Retorna a textura do Tile para construir a Textured2DMesh.
`component` | Retorna ou seta o componente que está no Tile.
`i` | Retorna ou seta a posição i do tile na matriz.
`j` | Retorna ou seta a posição j do tile na matriz.


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



# Agradecimentos
* Ana Luisa Holthausen de Carvalho (arte do jogo)


