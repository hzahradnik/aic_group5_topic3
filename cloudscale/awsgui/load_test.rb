require 'em-http-request'


File.open('../SentimentAnalysis2.0/src/main/resources/task_2.txt', 'r') do |f1|

      EventMachine.run {



        while line = f1.gets do
          p "requesting " + line
          http=EventMachine::HttpRequest.new('http://ec2-54-206-49-26.ap-southeast-2.compute.amazonaws.com:8080/api/sentiment', :inactivity_timeout => 0).get(:query => {'taskword' => line})
          http.callback {
            p http.response_header.status
            p http.response_header
            p http.response
          }
          sleep(1000)
        end


    }



end

