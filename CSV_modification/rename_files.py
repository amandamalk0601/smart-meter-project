import os

directory = os.getcwd() + '/1h_data'
renamed_directory = directory
counter = 1

for filename in os.listdir(directory):
    if filename.endswith('.csv'):
        new_filename = f'household{counter}.csv'
        counter += 1
        original_path = os.path.join(directory, filename)
        new_path = os.path.join(renamed_directory, new_filename)
        os.rename(original_path, new_path)
