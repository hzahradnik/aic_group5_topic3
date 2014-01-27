$( function( ) {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket;
    var analyzeSocket = new WS("@routes.Application.analyze().webSocketURL(request)");
    var btn = $('#btn-analyze');
    var result = $( "#result" );

    function startAnalyzing( ) {
        btn.button('loading');

        analyzeSocket.send('start');
        result.hide( );
    }

    function messageReceived( event ) {
        btn.button('reset');
        parseMessage( event.data );

        console.log( event );
    }

    function parseMessage( msg ) {
        var data;

        try {
            data = JSON.parse( msg );
        } catch( e ) {
            data = { error: true };
        }

        if( data.error ) {
            result.text( "Error, please try again!" );

            result.addClass( "error" );
            result.removeClass( "result" );
        } else {
            result.text( data.result );

            result.addClass( "result" );
            result.removeClass( "error" );

            console.log( data );
        }

        result.show( );
    }

    analyzeSocket.onmessage = messageReceived;

    btn.click( function() {
        startAnalyzing( );
    } );
} );