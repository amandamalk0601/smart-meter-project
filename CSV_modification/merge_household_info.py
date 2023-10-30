import pandas as pd
import os

# info_file contains the additional household information which will be merged
info_file = os.getcwd() + "/household-information.csv"

# in_dir is the source directory, containing all individual household csv files
in_dir = os.getcwd() + "/Reduced_CSV"

# out_dir is the target location for all merged csv files
out_dir = os.getcwd() + "/Merged_CSV"

# Read household information data
household_info = pd.read_csv(info_file)

# Loop over all csv files in the directory
for filename in os.listdir(in_dir):

    # Only process csv files
    if filename.endswith(".csv") and filename != "household-information.csv":
        
        file_path = os.path.join(in_dir, filename)
        # Load indivdual household data
        household_consumption_data = pd.read_csv(file_path)

        # Truncate .csv extension to get the userId
        user_id = filename[:-4]

        # Add a 'userId' column to the household data
        household_consumption_data['userId'] = user_id

        # Extract household info for specific user
        user_info = household_info[household_info['userId'] == user_id]
        
        # Merge the household data with tehe user's info
        merged = household_consumption_data.merge(user_info, how='left', left_on='userId', right_on='userId')

        # Save the merged data to a new csv file
        merged.to_csv(out_dir+f"merged_{user_id}.csv", index=False)
