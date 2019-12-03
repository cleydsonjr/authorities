import groovyx.net.http.URIBuilder
import org.codehaus.jackson.map.ObjectMapper

//https://www.googleapis.com/customsearch/v1?q=%22RONALDO%20RAMOS%20CAIADO%22&cx=014097292584411445814%3Ash5uhwve2ei&searchType=image&key=AIzaSyC159deHiupyQXWUfPXviv5nCv6hyIR9qw

objectMapper = new ObjectMapper()

String cx = ''
String key = ''

File outputFotosDir = new File('output_fotos')
File resultFotosDir = new File('result_fotos')
outputFotosDir.mkdirs()
resultFotosDir.mkdirs()

File nomes = new File('nomes.txt')
nomes.eachLine { String nome ->
	try {
		File thumbOut = new File(outputFotosDir, nome + '_thumb.jpeg')
		File imageOut = new File(outputFotosDir, nome + '.jpeg')
		File resultOut = new File(resultFotosDir, nome + '.json')

		String q = '"' + nome + '"'
		URIBuilder searchUri = new URIBuilder('https://www.googleapis.com')
		searchUri.setPath("/customsearch/v1")
		searchUri.addQueryParam('q', q)
		searchUri.addQueryParam('cx', cx)
		searchUri.addQueryParam('searchType', 'image')
		searchUri.addQueryParam('key', key)

		println('BUSCANDO ' + q + '...')
		String json = resultOut.exists() ? resultOut.text : searchUri.toURL().getText()
		resultOut.text = json

		Map result = objectMapper.readValue(json, Map)

		List<Map> items = (List<Map>) result['items']

		Map item = items.find { it['mime'] == 'image/jpeg' }
		if (!item) {
			item = items[0]
		}

		String link = item['link']
		String thumbnail = item['image']['thumbnailLink']

		println('LINK OBTIDO: ' + link)
		println('THUMBNAIL: ' + thumbnail)

		println()
		if (item['mime'] == 'image/jpeg' && !imageOut.exists()) {
			println('BAIXANDO LINK ...')
			URL imageUrl = new URL(link)
			byte[] imageBytes = imageUrl.getBytes()
			imageOut.bytes = imageBytes
		} else {
			println('Imagem ja baixada.. Pulando etapa')
		}

		println()

		if (!thumbOut.exists()) {
			println('BAIXANDO THUMB ...')
			URL thumbnailUrl = new URL(thumbnail)
			byte[] thumbBytes = thumbnailUrl.getBytes()
			thumbOut.bytes = thumbBytes
		} else {
			println('Thumb ja baixado.. Pulando etapa')
		}
	} catch (Exception e) {
		println("FALHOU" + e.message)
	}

	println('-----------------------------------------------------------------------------------------')
}
