from kafka import KafkaProducer
from concurrent.futures import ThreadPoolExecutor
import pandas as pd
import os
import time

# Adjust the folder path to the folder containing the data
folder_path = os.getcwd()+ "/10_min_data/"

def send_data(file_id):
    # Initialize a Kafka producer
    producer = KafkaProducer(bootstrap_servers='localhost:9092')
    
    # read csv files
    df = pd.read_csv(os.path.join(folder_path, f'household{file_id}.csv'))

    # send every row (from the csv file) to the producer
    for _, row in df.iterrows():
        producer.send(f'household{file_id}', ','.join(map(str, row.values)).encode('utf-8'))
        time.sleep(2)
    print("Successfully send household data!")
    producer.close()

with ThreadPoolExecutor(max_workers=9) as executor:
    for file_id in range(1,10):
        executor.submit(send_data, file_id)
