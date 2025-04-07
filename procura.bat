@echo off
setlocal enabledelayedexpansion

for /R %%f in (*.java,*.properties, pom.xml, product.proto) do (
    type "%%f" >> fontes.txt
)

echo Conteudo dos arquivos .java copiados para fontes.txt
pause