require 'net/http'
require 'cgi'
require 'thread'

semaphore = Mutex.new
keywords = IO.read( "../SentimentAnalysis2.0/src/main/resources/task_2.txt" ).lines.to_a.map { |x| x.to_s.strip }

def fetch_tag( semaphore, keyword )
  semaphore.synchronize { puts "request #{keyword}" }
  url = URI.parse( "http://ec2-54-206-49-26.ap-southeast-2.compute.amazonaws.com:8080/api/sentiment" )
  req = Net::HTTP::Get.new(url.path+"?taskword=#{CGI::escape( keyword )}")
  res = Net::HTTP.start(url.host, url.port) {|http|
    http.read_timeout = 500
    semaphore.synchronize { puts "before request #{keyword}" }
    http.request(req)
  }
  semaphore.synchronize { puts "#{keyword} -> " + res.body }
end

threads = []
puts "ARGV=#{ARGV.inspect}"

kws = if ARGV.length == 1
  count = ARGV[ 0 ].to_i
  keywords[0...count]
else
  from = ARGV[ 0 ].to_i
  to = ARGV[ 1 ].to_i
  to = from if to == 0
  
  keywords[ from..to ]
end

puts "kws= #{kws.inspect}"

kws.each do |kw|
  t = Thread.new do
    fetch_tag( semaphore, kw )
  end

  threads.push( t )
end

threads.each( &:join )
