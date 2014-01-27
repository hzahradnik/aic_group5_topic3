console.log( "analyze.js" );

function startAnalyzing( ) {
	$( '#btn-analyze' ).button( 'loading' );
	$( '#result' ).hide( );
	
	sendAnalyzeMessage( true );
}

function errorOccured( data ) {
	var result = $( '#result' );
	var btn = $( '#btn-analyze' );
	
	btn.button( 'reset' );
	
    result.text( "Error, please try again!" );
    result.addClass( "error" );
    result.removeClass( "result" );
    result.show( );
}

function sendAnalyzeMessage( start ) {

	
	$.ajax( {
		url: '/analyze',
		type: "POST",
		data: { start: start === true },
		success: function( data ) {
			if( data.error ) {
				errorOccured( data );
			} else if( data.result === "analyzing" ) {
				setTimeout( sendAnalyzeMessage, 1000 );
			} else {
				var result = $( '#result' );
				var btn = $( '#btn-analyze' );
				
				btn.button( 'reset' );
				
	            result.text( data.result );
	            result.addClass( "result" );
	            result.removeClass( "error" );
	            result.show( );
			}
		},
		error: function( ) {
			errorOccured( );
		},
		dataType: 'json'
	} );	
}

$( function( ) {
	$( "#btn-analyze" ).click( function( ) {
		startAnalyzing( );
	} );
} );