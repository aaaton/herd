var bratLocation = '';
head.js(
    // External libraries
    bratLocation + '/js/lib/jquery.min.js',
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

var docData = {
    // Our text of choice
    text     : "Ed O'Kelley was the man who shot the man who shot Jesse James. Test.",
    // The entities entry holds all entity annotations
    entities : [
        ['T1', 'Person', [[0, 11]] ],
        ['T2', 'Person', [[20, 23]] ],
        ['T3', 'Person', [[37, 40]] ],
        ['T4', 'Person', [[50, 61]] ],
        ['P1', 'Paragraph', [[0, 61]] ],
        ['E1', 'urn:wikidata:Q34', [[0, 11]] ]
    ],
    token_offsets: [

    ],
    sentence_offsets: [
        [0,62],
        [63, 68]
    ],
    comments: [
        ["T1", "AnnotatorNotes", "3.4509"],
        ["T2", "AnnotatorNotes", "3.42"],
        ["T4", "HTML", "<b>Ordering:</b><ol><li>Ett</li><li>Tva</li><li>Tre</li></ol>"]
    ]
    /*comment_types: [
        "Person": {"borderColor": "darken", "arrowHead": "triangle,5", "name": "Person", "color": "black", "labels": ["Person", "Per"], "unused": true, "bgColor": "lightgreen", "type": "Person", "fgColor": "black"}
    ]*/
};

var collData = {
    entity_types: [ {
            type   : 'Person',
            /* The labels are used when displaying the annotation, in this case
                we also provide a short-hand "Per" for cases where
                abbreviations are preferable */
            labels : ['Person', 'Per'],
            // Blue is a nice colour for a person?
            bgColor: '#7fa2ff',
            // Use a slightly darker version of the bgColor for the border
            borderColor: 'darken'
    },
    {
                type   : 'Paragraph',
                /* The labels are used when displaying the annotation, in this case
                    we also provide a short-hand "Per" for cases where
                    abbreviations are preferable */
                labels : ['Paragraph', 'Par'],
                bgColor: '#ee0000',
                // Use a slightly darker version of the bgColor for the border
                borderColor: 'darken'
    },
    {
                    type   : 'urn:wikidata:Q34',
                    /* The labels are used when displaying the annotation, in this case
                        we also provide a short-hand "Per" for cases where
                        abbreviations are preferable */
                    labels : ['Q34'],
                    bgColor: '#ee0000',
                    // Use a slightly darker version of the bgColor for the border
                    borderColor: 'darken'
    }]
};

head.ready(function() {
    Util.embed(
        // id of the div element where brat should embed the visualisations
        'main-doc',
        // object containing collection data
        collData,
        // object containing document data
        docData,
        // Array containing locations of the visualisation fonts
        webFontURLs
    );
});