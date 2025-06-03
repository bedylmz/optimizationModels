#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include <time.h>

void saveTXT(char* filename, double * values, int dimension)
{
    FILE *file = fopen(filename, "a");
    if (file == NULL) {
        perror("Error opening file");
        return;
    }

    for (int i = 0; i < dimension; i++) {
        fprintf(file, "%f ", values[i]);
    }
    fprintf(file, "\n");

    fclose(file);
}

void main()
{
    double * values = calloc(15, sizeof(double));
    saveTXT("data/Test_Exe.txt", values, 15);
}
