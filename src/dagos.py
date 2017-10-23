import yara  # importing yara library
import sys   # for commandline argument

supplied_data = '' # initializing with null
if len(sys.argv) > 1:				# grab the value of commandline argument
	supplied_data = sys.argv[1]
#supplied argument is ascii, this library automatically convert it to hex and matches for both ascii and HEX

return_value='nf'   #default value nf=not found
# compile yara rules found in this path
rule = yara.compile(filepath='rules.yar')
# match rules with passed arguments
matches = rule.match(data=supplied_data)

#return value  is a python list which is empty is no match found
if len(matches) > 0:
	return_value = 'f'   # found

print(return_value)

#print(matches)
#print(matches[0].rule)
#print(matches[0].tags)
#print(matches[0].strings)