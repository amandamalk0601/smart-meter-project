import os
import pandas as pd

# in_dir is the source directory, containing all original csv files
in_dir =  os.getcwd()+ "/1h_data/"

# out_dir is the target location for all processed csv files
out_dir =  os.getcwd()+ "/20_min_data/"

# start_date marks the beginning condition of the extraction process
start_date =  "2019-12-24T00:00:00"

# end_date is defined as the end of the process
end_date =  "2019-12-24T00:20:00"

# Define chunk size (number of rows to read at a time) 
chunk_size = 10000

for file in os.listdir(in_dir):
    if file.endswith(".csv"):
        file_path = os.path.join(in_dir, file)
        save_path = os.path.join(out_dir, file)

        for chunk in pd.read_csv(file_path, chunksize=chunk_size):

            # Convert the first column to datetime so there is no TypeError 
            chunk['date'] = pd.to_datetime(chunk['date'])
            
            # Keep rows where the date is between start_date and end_date
            chunk = chunk[(chunk['date'] >= start_date) & (chunk['date'] < end_date)]
   
            # If chunk is found that is between start_date and end_date,
            # it's checked if the output file already exists and writes the header, 
            # otherwise rows are written
            if not chunk.empty:
                if not os.path.isfile(save_path):
                    chunk.to_csv(save_path, index=False)
                else:
                    chunk.to_csv(save_path, mode='a', header=False, index=False)