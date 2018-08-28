'use strict';
module.exports = function(app) {
  var controller = require('../controllers/metadataController');

  // controller Routes
  app.route('/api/meta-data/units')
    .get(controller.getUnits)
};