var bratLocation = '';
head.js(
    // External libraries
    //bratLocation + '/js/lib/jquery.min.js',
    bratLocation + '/js/lib/jquery.browser.min.js',
    bratLocation + '/js/lib/jquery.svg.min.js',
    bratLocation + '/js/lib/jquery.svgdom.min.js',

    // brat helper modules
    bratLocation + '/js/src/configuration.js',
    bratLocation + '/js/src/util.js',
    bratLocation + '/js/src/annotation_log.js',
    bratLocation + '/js/lib/webfont.js',

    // brat modules
    bratLocation + '/js/src/dispatcher.js',
    bratLocation + '/js/src/url_monitor.js',
    bratLocation + '/js/src/visualizer.js',
    bratLocation + '/js/src/visualizer_ui.js'
);

var webFontURLs = [
    bratLocation + '/static/fonts/Astloch-Bold.ttf',
    bratLocation + '/static/fonts/PT_Sans-Caption-Web-Regular.ttf',
    bratLocation + '/static/fonts/Liberation_Sans-Regular.ttf'
];

function formatProperties(properties) {
    var text = ["<table class='prop-table'><thead><tr>"];

    $.each(properties, function(key, value) {
        text.push("<th>" + key + "</th>");
    });

    text.push("</tr></thead><tbody><tr>");

    $.each(properties, function(key, value) {
        text.push("<td>" + value + "</td>");
    });

    text.push("</tr></tbody></table>");

    return text.join("");
}

function createBratModel(doc, showLayers) {
    var ColorArray = []
    for(var i = 0; i < 360; i += 6) {
        ColorArray.push(Util.rgbToStr(Util.hslToRgb([i / 360, 0.5, 0.75])))
    }

    for(var i = 0; i < 360; i += 6) {
        ColorArray.push(Util.rgbToStr(Util.hslToRgb([i / 360, 0.25, 0.75])))
    }

    for(var i = 0; i < 360; i += 6) {
            ColorArray.push(Util.rgbToStr(Util.hslToRgb([i / 360, 1.0, 0.75])))
    }

    var docData = {
        text: doc.text,
        entities: [],
        sentence_offsets: [],
        comments: []
    }

    var collData = {
        entity_types: [],
    }

    for(var i = 0; i < doc.sentences.length; i += 1) {
        docData.sentence_offsets.push([doc.sentences[i], doc.sentences[i+1]]);
    }

    var labelSet = {}

    $.each(showLayers, function(key, value) {
        var nodes = doc.layers[value];

        for(var i = 0; i < nodes.length; i++) {
            var node = nodes[i];
            labelSet[node.label] = true;
            docData.entities.push([value + i, node.label, [[node.start, node.end]] ]);
            docData.comments.push([value + i, "HTML", formatProperties(node.properties)]);
        }
    });

    $.each(labelSet, function(value, key) {
        collData.entity_types.push({
            type   : value,
            /* The labels are used when displaying the annotation, in this case
                we also provide a short-hand "Per" for cases where
                abbreviations are preferable */
            labels : [value],
            // Blue is a nice colour for a person?
            bgColor: ColorArray[Math.floor(Math.random() * ColorArray.length)], // '#7fa2ff',
            // Use a slightly darker version of the bgColor for the border
            borderColor: 'darken'
        });
    });

    return {
        docData: docData,
        collData: collData
    }
}

head.ready(function() {
    var datamodel = JSON.parse($("#doc-data").html());
    var model = createBratModel(datamodel, ["Token", "Anchor", "NamedEntity"]);

    $('#annotation-layers').dropdown({
        allowAdditions: true
    });

    var layerSelect = $('#annotation-layers');

    $.each(datamodel.layers, function(key,value) {
        $(layerSelect[0]).append('<option value="' + key + '">' + key + '</option>')
    });

    layerSelect.dropdown("set selected", "Token")
    layerSelect.dropdown("set selected", "Anchor")
    layerSelect.dropdown("set selected", "NamedEntity")

    Util.embed(
        // id of the div element where brat should embed the visualisations
        'main-doc',
        // object containing collection data
        model.collData,
        // object containing document data
        model.docData,
        // Array containing locations of the visualisation fonts
        webFontURLs
    );
});