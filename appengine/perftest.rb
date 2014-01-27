require 'net/http'
require 'cgi'
require 'thread'
require 'json'

if ARGV.include?( "--live" )
  domain = "aic-group5-topic3-ws13.appspot.com"
  ARGV.delete( "--live" )
else
  domain = "localhost:8888"
end

$url_analyze = URI.parse( "http://#{domain}/analyze" )
$url_register = URI.parse( "http://#{domain}/register" )

semaphore = Mutex.new
keywords = IO.read( "../cloudscale/analysis/src/main/resources/task_2.txt" ).lines.to_a.map { |x| x.to_s.strip }

def fetch_tag( semaphore, keyword )
  semaphore.synchronize { puts "request #{keyword}" }
  response = Net::HTTP.post_form( $url_register, {
    "name" => keyword,
    "password1" => "123",
    "password2" => "123"
  } )

  i = 0

  begin
    sleep( 1 ) if i > 0
    http = Net::HTTP.new( $url_analyze.host, $url_analyze.port )

    request = Net::HTTP::Post.new( $url_analyze.request_uri )
    request.set_form_data( { 'name' => keyword } )

    response = http.request( request )
    data = JSON.parse( response.body )

    i += 1
  end while data[ 'result' ] == 'analyzing'

  semaphore.synchronize { puts "#{keyword} => #{data.inspect}" }
end

threads = []

kws = if ARGV.length == 1
  count = ARGV[ 0 ].to_i
  keywords[0...count]
else
  from = ARGV[ 0 ].to_i
  to = ARGV[ 1 ].to_i
  to = from if to == 0

  keywords[ from..to ]
end

puts "kws.length = #{kws.length}"
puts "kws=#{kws.inspect}"
start = Time.now.to_f

kws.each do |kw|
  t = Thread.new do
    fetch_tag( semaphore, kw )
  end

  threads.push( t )
end

threads.each( &:join )
duration = Time.now.to_f - start
puts "duration = #{duration.inspect}"
