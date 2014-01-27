console.log( "analyze.js" );

function startAnalyzing( ) {
	$( '#btn-analyze' ).button( 'loading' );
	$( '#result' ).hide( );
	
	sendAnalyzeMessage( );
}

function sendAnalyzeMessage( ) {
	var result = $( '#result' );
	var btn = $('#btn-analyze');
	
	$.post( '/analyze', function( data ) {
		if( data.result === "analyzing" ) {
			setTimeout( sendAnalyzeMessage, 1000 );
		} else {
			btn.button( 'reset' );
            result.text( data.result );

            result.addClass( "result" );
            result.removeClass( "error" );
            result.show( );
		}
	}, 'json' );	
}

$( function( ) {
	$( "#btn-analyze" ).click( function( ) {
		startAnalyzing( );
	} );
} );