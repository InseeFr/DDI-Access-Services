'use strict';
var xml = require('xml');
var Unit = require("../models/metadataModel.js");

exports.getUnits = function(req, res) {
    let unit1 = new Unit("http://id.insee.fr/unit/euro", "€");
    let unit2 = new Unit("http://id.insee.fr/unit/keuro", "k€");
    let unit3 = new Unit("http://id.insee.fr/unit/percent", "%");
    var units = [unit1,unit2,unit3];
    res.json(units);
    res.status(200).end();
};
