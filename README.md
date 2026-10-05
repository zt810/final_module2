javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName

java -cp out bankaccount.Main