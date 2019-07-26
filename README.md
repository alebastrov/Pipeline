# Pipeline
You may want to create your own pipe processor for any text file (for instance log file)

The main idea is based on *nix pipes - each processed line from file may be processed with several workers.

An built-in example demonstrates the login:
We have a CSV like text file with 3 fields in each row, middle field should be multiplied by 10.
File is not strongly follow the description above: some rows are empty, some do not contain exact 3 field, etc.
Our pipes are connected in order to reach the goal: multiply middle field to 10.
In order to achieve that we have to:
- read file line by line
- skip line with comments
- verify line is CSV with 3 fields
- parse line as CSV and select middle field
- trim field
- make sure field can be converted to a number
- convert field into number
- multiple field to 10
- output the result

Looks not to simple?

Lets take a look at second rule (skip comment line)
You may reach this by writing something like 
```
lineReader.addToPipeline(new Pipeline<String, String>("Skip comments", String.class, String.class) {
            @Override
            public boolean isAllowed(String line) {
                return !line.trim().startsWith("#");
            }
        });
```
This rule says to pass only the line to next if it meets the rule (it does not start with '#' character). 
We skip processing rule here because it does nothing interesting here (it takes a line and returns it without any transformation)
It might look as 
```
public String process(String line) {return line;}
````

Next our step is to parse line from CSV format
```
lineReader.addToPipeline(new Pipeline<>("Parse as CSV", String.class, String[].class, new PropertiesFileConfiguration()) {
            @Override
            public String[] process(String line) {
                return CsvUtil.csvLineParser(line);
            }
        });        
```

That rule says to take a line and parse it as CSV line. If it cannot be parsed - next pipe will not be executed. 
The result of operation here is not the line itself - it's array of fields (you may see that in third argument of pipeline's constructor). We skip the isAllowed method because we do not need here any conditional rules to skip the line.
Though it might nbe shown like 
```
public boolean isAllowed(String line) {
                return line.contains(",") || line.contains(";");
            }
```

Our next steps look similar to these.

The more processing rule we have, the more operations we do.

Besides that the program shows some statistics here - position if file, how many chars are processed and status of each line
