var express = require('express'),
  app = express(),
  port = process.env.PORT || 3000;
  bodyParser = require('body-parser');


  app.use(bodyParser.urlencoded({ extended: true }));
  app.use(bodyParser.json());
  
  
  var routes = require('./api/routes/metadataRoutes'); //importing routes
  routes(app); //register routes
app.listen(port);

console.log('Mock Colectica API server started on: ' + port);