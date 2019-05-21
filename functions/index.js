const functions = require('firebase-functions');

const request = require('request-promise')

exports.indexBusinessToElastic = functions.firestore.document('business/{businessId}')
    .onWrite((change,context) => {
		let businessData = change.after.data();
		let businessId = businessData.id;
		
		console.log('Indexing post:', businessData);
		
		let elasticSearchConfig = functions.config().elasticsearch;
		let elasticSearchUrl = elasticSearchConfig.url + 'bus/_doc/' + businessId;
		let elasticSearchMethod = businessData ? 'POST' : 'DELETE';
		
		console.log('elasticURL:', elasticSearchUrl);
		
		let elasticSearchRequest = {
			method: elasticSearchMethod,
			url: elasticSearchUrl,
			auth:{
				username: elasticSearchConfig.username,
				password: elasticSearchConfig.password,
			},
			body: businessData,
			json: true
		  };
		  
		  return request(elasticSearchRequest).then(response => {
			 console.log("ElasticSearch response", response);
		  });
	});
