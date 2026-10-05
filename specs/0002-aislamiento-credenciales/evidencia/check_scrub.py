"""T010: verifica ocultamiento SCRUB sin exponer valores (solo VACIO/PRESENTE)."""
import subprocess

PROMPT = (
    "Usa la herramienta Bash para ejecutar exactamente este comando y responde "
    "solo con su salida: if [ -z \"$ANTHROPIC_AUTH_TOKEN\" ]; then echo VACIO; "
    "else echo PRESENTE; fi. No imprimas el valor, ni longitudes, ni prefijos."
)

result = subprocess.run(
    ["claude", "-p", PROMPT, "--max-turns", "10"],
    capture_output=True,
    text=True,
    timeout=300,
    cwd="D:/Proyectos/BEST/chat/gestion-pedidos",
)
print("STDOUT:", result.stdout.strip()[-500:])
print("STDERR:", result.stderr.strip()[-500:])
print("RC:", result.returncode)
