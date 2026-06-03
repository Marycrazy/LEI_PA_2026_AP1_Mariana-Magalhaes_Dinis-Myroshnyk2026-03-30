FROM nixos/nix

RUN echo 'experimental-features = nix-command flakes' >> /etc/nix/nix.conf

WORKDIR /docs

COPY . .

RUN nix develop --command bash -c "cd ruby && bundle install"

ENTRYPOINT ["nix", "develop", "--command", "bash", "-c"]
CMD ["asciidoctor-pdf -r ./ruby/converter.rb -D out *.adoc && rm -rf .vscode"]