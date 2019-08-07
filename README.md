# TMDb
Aplicativo Android feito em Kotlin para mostrar os próximos lançamentos e buscar filmes utilizando a API do TMDb, além de mostrar páginas de detalhes dos filmes.

## Utilização
Para utilizar, é importante configurar a sua API key do TMDb no gradle.properties do seu computador. Para isso, adicione a seguinte linha no seu gradle.properties:

TMDB_API_KEY = "sua_api_aqui"

Sem a API key configurada não será possível fazer chamadas.

## Bibliotecas utilizadas
Foram utilizadas as seguintes bibliotecas neste projeto:
- Retrofit+Okhttp: utilizados para fazer as requisições para a API;
- Androix Lifecycle: usado nos ViewModels;
- Ion: usado para carregar e fazer cache das imagens dos posteres dos filmes;
- Room: para fazer cache das informações dos filmes e dos gêneros;
- Mockito: nos testes unitários para mockar valores retornados pelo serviço e pelo banco de dados do Room;
- RxJava2: utilizado como respostas das chamads do Retrofit e das funções do Room.

## Funcionalidades
Foi utilizado o Rx para encadear chamadas tanto dentro do Room quanto na API. No caso dos lançamentos, é feita uma busca inicial na API, caso ela falhe por qualquer problema, será disparada uma chamada ao Room para tentar buscar os últimos lançamentos que foram baixados com sucesso pela API. Toda vez que ele faz uma chamada inicial com sucesso ele apaga todos os filmes guardados no banco e adiciona os novos no lugar. Quando uma chamada de paginação é feita com sucesso, ele apenas adiciona os resultados no banco.

O processo inverso foi usado na tela de detalhes para buscar os gêneros. Como a API não retorna os nomes dos gêneros de filmes, retornando apenas os ids, foi necessário buscar na API de gêneros todos os existentes. Para isso, primeiro é feita uma busca no Room para retornar todos os gêneros existentes. Se nenhum for retornado, uma chamada é feita na API para buscar todos os possíveis gêneros. Quando a chamada é feita com sucesso, os gêneros são salvos no Room e nunca mais são buscados novamente na API. Caso algum erro ocorra, a informação de gêneros do filme ficará em branco.

Para a busca foi utilizado um SearchView onde a query pode ser digitada. Ao confirmar a busca pelo teclado, a chamada na API é feita.
Foram feitos para todos os casos tratamentos de erros de conexão e erros generalizados. Para os lançamentos e a busca, foi utilizado scroll infinito com paginação no RecyclerView. Mensagens de lista vazia foram criados para os casos de ser a primeira vez que o app é aberto e nenhum lançamento foi salvo ainda e para casos em que a query não retorna nenhum filme na busca.

## Testes
Foram realizados testes unitários em cima dos ViewModels. Foram testados os diferentes retornos possíveis da API e do Room, assim como casos de erros já esperados.
