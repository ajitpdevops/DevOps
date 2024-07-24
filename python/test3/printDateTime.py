from datetime import datetime

date_time = datetime.now()
str_time = date_time.strftime(f"[%Y-%m-%d %H:%M:%S]")
print(f"{str_time}: This is a test message.")