#!/bin/bash

bootstrap_servers="localhost:9092"
topic_names=("household1" "household2" "household3" "household4" "household5" "household6" "household7" "household8" "household9")
current_dir=$(pwd)

# Delete the topics
for topic in "${topic_names[@]}"; do
    $current_dir/kafka-3.4.0-src/bin/kafka-topics.sh --bootstrap-server "$bootstrap_servers" --delete --topic "$topic"
done

# Recreate the topics
for topic in "${topic_names[@]}"; do
    $current_dir/kafka-3.4.0-src/bin/kafka-topics.sh --bootstrap-server "$bootstrap_servers" --create --topic "$topic" --partitions 1 --replication-factor 1
done

echo "finished cleaning"
