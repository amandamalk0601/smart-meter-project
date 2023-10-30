import os
import pandas as pd

# in_dir is the source directory, containing all original csv files
in_dir = os.getcwd() + "/Full_CSV"

# out_dir is the target location for all processed csv files
out_dir = os.getcwd() + "/Reduced_CSV"

# start_date marks the beginning condition of the extraction process
start_date =  "2019-12-24T00:00:00"

# end_date is defined as the end of the process
end_date =  "2019-12-25T00:00:00"

# Define chunk size (number of rows to read at a time) 
chunk_size = 10000

# Define column names
column_names = ["date", "timestamp", "energy", "power", "power1", "power2", "power3", "unknown"]

# Define which columns to include in reduced CSV
cols = ["date", "timestamp", "energy", "power", "power1", "power2", "power3"]

for file in os.listdir(in_dir):
    if file.endswith(".csv"):
        file_path = os.path.join(in_dir, file)
        save_path = os.path.join(out_dir, file)

        for chunk in pd.read_csv(file_path, chunksize=chunk_size,  skiprows=1, header=None):

            # Assign column names to the chunk
            chunk.columns = column_names

            # Convert the first column to datetime so there is no TypeError 
            chunk['date'] = pd.to_datetime(chunk['date'], format='ISO8601')
            
            # Keep rows where the date is between start_date and end_date
            chunk = chunk[(chunk['date'] >= start_date) & (chunk['date'] < end_date)]
   
            # If chunk is found that is between start_date and end_date,
            # it's checked if the output file already exists and writes the header, 
            # otherwise rows are written
            if not chunk.empty:
                if not os.path.isfile(save_path):
                    chunk.to_csv(save_path, index=False, columns=cols)
                else:
                    chunk.to_csv(save_path, mode='a', header=False, index=False, columns=cols)
