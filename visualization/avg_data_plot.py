import pandas as pd
import matplotlib.pyplot as plt
import os


# Specify the header names
header = ['date/timestamp','userId', 'timestamp', 'average_power']

file_path = os.getcwd()+ "/sorted1h.csv"
# Load the data into a pandas DataFrame
df = pd.read_csv(file_path, names=header, header=None)
# Convert timestamp to datetime
df['timestamp'] = pd.to_datetime(df['timestamp'], unit='ms')

# Plot a line for each user
fig, ax = plt.subplots()
i = 1
for user in df['userId'].unique():
    user_data = df[df['userId'] == user]
    name = "household" + str(i)
    ax.plot(user_data['timestamp'], user_data['average_power'], label=name)
    i= i+1

# Configure the plot
ax.set(xlabel='time (s)',  ylabel='peak power in watts',#ylabel='avg power in watts',
       title='peak power consumption per 2 minute window')
       # title='Average power consumption per 3 minute window')
ax.grid()

# Show a legend
plt.legend()

# Show the plot
plt.show()
