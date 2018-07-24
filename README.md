# streamsets-datacollector-syslog-destination

This Syslog destination, along with the built-in UDP/TCP origins can be used to build pipelines to intelligently route and filter Syslog messages.

This allows you to perform a Syslog man-in-the-middle and act on messages as they're received. Perhaps some chatty logging has used all your Splunk quota and you'd like to route the low-value messages to your Hadoop cluster and send the rest to your SIEM. Or you'd like to watch for some condition and send alerts via SMS or Slack.

Credit to [Dean Proctor](https://github.com/deanproctor) who wrote most of this.
