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

var collData = {
    entity_types: [ {
            type   : 'Name',
            labels : ['Named Entity', 'NE'],
            bgColor: '#7fa2ff',
            borderColor: 'darken'
    } ]
};




/*
var docData = {
    text     : "Ed O'Kelley was the man who shot the man who shot Jesse James.",
    entities : [
        ['T1', 'Name', [[0, 11]]],
        ['T2', 'Name', [[50, 61]]],
    ],
};
*/