import matplotlib.pyplot as plt

# Load the publish times from the file
with open('2s.txt', 'r') as f:
    latencies = [float(line.strip()) for line in f]

# Generate a sequence of numbers for the x-axis (message numbers)
message_numbers = list(range(1, len(latencies) + 1))

# Plot the data
plt.plot(message_numbers, latencies, color='#A8DADC')  # Use a hex color for the plot line
plt.xlabel('Message Number')
plt.ylabel('Publish Time (ms)')
plt.title('2s delay: Latency of each message')

# Change the color of the x and y axis
ax = plt.gca()
ax.spines['bottom'].set_color('#457B9D')
ax.spines['left'].set_color('#457B9D')
ax.xaxis.label.set_color('#457B9D')
ax.yaxis.label.set_color('#457B9D')
ax.title.set_color('#457B9D')
plt.tick_params(axis='x', colors='#457B9D')
plt.tick_params(axis='y', colors='#457B9D')

plt.show()
