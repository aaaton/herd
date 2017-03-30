head.ready(function() {
    if(docData) {
    console.log("lol");
        Util.embed(
                'tagged_text', //div id
                collData, //data structure
                docData, //document data
                webFontURLs //bs
        );
    }

});