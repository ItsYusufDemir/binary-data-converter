# binary-data-converter
This is a group project of 4 people. The aim is coding a binary data converter that converts binary into (signed | unsigned) integer or floating point number.
Authors:

## How it works?

The input is a line of bytes. Each line contains 12 bytes. Inputs are given in hexadecimal.

For example: f0 90 01 40 03 00 ff ff 00 00 e0 7f

In the beginning of the program, the user prompts the input file name, byte order (little endian -> l or big endian -> b), data type (float, int or usigned) and lastly the data size.

For instance, if it is float and the data size is 4 bytes, the program takes 4 bytes of the line and interprets it as a float number. During this process, byte order is also considered.

At the end, the numbers are printed both consolen and output.txt.
