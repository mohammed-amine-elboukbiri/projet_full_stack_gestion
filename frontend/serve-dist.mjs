import { createReadStream, existsSync } from "node:fs";
import { readFile } from "node:fs/promises";
import http from "node:http";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const rootDir = path.join(__dirname, "dist");
const host = "127.0.0.1";
const port = 5173;

const contentTypes = new Map([
  [".html", "text/html; charset=utf-8"],
  [".js", "text/javascript; charset=utf-8"],
  [".css", "text/css; charset=utf-8"],
  [".json", "application/json; charset=utf-8"],
  [".svg", "image/svg+xml"],
  [".png", "image/png"],
  [".jpg", "image/jpeg"],
  [".jpeg", "image/jpeg"],
  [".ico", "image/x-icon"]
]);

const server = http.createServer(async (request, response) => {
  const url = new URL(request.url ?? "/", `http://${host}:${port}`);
  const requestedPath = decodeURIComponent(url.pathname);
  const candidatePath = path.normalize(
    path.join(rootDir, requestedPath === "/" ? "index.html" : requestedPath.slice(1))
  );

  const safePath = candidatePath.startsWith(rootDir) ? candidatePath : path.join(rootDir, "index.html");
  const filePath = existsSync(safePath) ? safePath : path.join(rootDir, "index.html");
  const extension = path.extname(filePath);

  try {
    const statFile = await readFile(filePath);
    response.writeHead(200, {
      "Content-Type": contentTypes.get(extension) ?? "application/octet-stream",
      "Content-Length": statFile.byteLength
    });
    createReadStream(filePath).pipe(response);
  } catch (error) {
    response.writeHead(500, { "Content-Type": "text/plain; charset=utf-8" });
    response.end(`Server error: ${error instanceof Error ? error.message : "unknown"}`);
  }
});

server.listen(port, host, () => {
  console.log(`Frontend disponible sur http://${host}:${port}`);
});
